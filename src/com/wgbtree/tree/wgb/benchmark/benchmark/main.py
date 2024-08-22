from sys import base_prefix

import boto3
import matplotlib.pyplot as plt
import random

tables = ["insert", "search", "searchMin", "searchMax", "depth"]
colors = ["red", "blue", "green", "orange", "purple", "brown", "pink", "gray", "olive", "cyan"]

dynamodb_client = boto3.client(
    'dynamodb',
    region_name='us-east-2',
    endpoint_url='http://localhost:8001',
    aws_access_key_id='FAKE',
    aws_secret_access_key='FAKE'
)


def fetch_all_tree_names():
    data = set()

    try:
        last_evaluated_key = None

        while True:
            if last_evaluated_key:
                response = dynamodb_client.scan(
                    TableName='search',
                    ExclusiveStartKey=last_evaluated_key  # For pagination
                )
            else:
                response = dynamodb_client.scan(
                    TableName='search'
                )

            items = response.get('Items', [])
            for item in items:
                data.add(item['tree_name']['S'])

            # Check if there are more items to retrieve
            last_evaluated_key = response.get('LastEvaluatedKey')
            if not last_evaluated_key:
                break

        return list(data)
    except Exception as e:
        print(f"Error fetching tree names: {e}")
        return []


def print_tree_ranking(min_count, max_count):
    for table in tables:
        data = fetch_data_from_table_in_range(table, min_count, max_count)
        print(f"\t{table.capitalize()} [{min_count}, {max_count}]:")

        cumulative_data = {}
        cumulative_data_per_charac = {}

        for point in data:
            _tree_name = point['tree_name']
            count = point['count']
            metric = point['metric']

            if "o:" in _tree_name:
                _order = "o-" + _tree_name.split("|o:")[1]
            elif "[o-" in _tree_name:
                _order = _tree_name.split("[o-")[1]
                _order = "o-" + _order.split("]")[0]
            else:
                _order = None

            if _order is not None:
                if _order not in cumulative_data_per_charac:
                    cumulative_data_per_charac[_order] = {'count_times_metric': 0, 'count_sum': 0}

                cumulative_data_per_charac[_order]['count_times_metric'] += count * metric
                cumulative_data_per_charac[_order]['count_sum'] += count

            if "[r-" in _tree_name:
                _rank = _tree_name.split("[r-")[1]
                _rank = "r-" + _rank.split("]")[0]
            else:
                _rank = None

            if _rank is not None:
                if _rank not in cumulative_data_per_charac:
                    cumulative_data_per_charac[_rank] = {'count_times_metric': 0, 'count_sum': 0}

                cumulative_data_per_charac[_rank]['count_times_metric'] += count * metric
                cumulative_data_per_charac[_rank]['count_sum'] += count

            if "[g-" in _tree_name:
                _group = _tree_name.split("[g-")[1]
                _group = "g-" + _group.split("]")[0]
            else:
                _group = "g-" + _tree_name

            if _group not in cumulative_data_per_charac:
                cumulative_data_per_charac[_group] = {'count_times_metric': 0, 'count_sum': 0}

            cumulative_data_per_charac[_group]['count_times_metric'] += count * metric
            cumulative_data_per_charac[_group]['count_sum'] += count

        average_metric_by_tree = {}
        for _tree_name, values in cumulative_data_per_charac.items():
            total_metric = values['count_times_metric']
            total_count = values['count_sum']
            average_metric = total_metric / total_count if total_count != 0 else 0
            average_metric_by_tree[_tree_name] = average_metric

        sorted_trees = sorted(average_metric_by_tree.items(), key=lambda x: x[1])

        for _i, (_tree_name, avg_metric) in enumerate(sorted_trees):
            print(f"\t\t{_i + 1}. {_tree_name}: {avg_metric:.2f}")

        print()
        print()

        for point in data:
            _tree_name = point['tree_name']
            count = point['count']
            metric = point['metric']

            if _tree_name not in cumulative_data:
                cumulative_data[_tree_name] = {'count_times_metric': 0, 'count_sum': 0}

            cumulative_data[_tree_name]['count_times_metric'] += count * metric
            cumulative_data[_tree_name]['count_sum'] += count

        average_metric_by_tree = {}
        for _tree_name, values in cumulative_data.items():
            total_metric = values['count_times_metric']
            total_count = values['count_sum']
            average_metric = total_metric / total_count if total_count != 0 else 0
            average_metric_by_tree[_tree_name] = average_metric

        sorted_trees = sorted(average_metric_by_tree.items(), key=lambda x: x[1])

        print(f"\tTop 10 trees:")
        for _i, (_tree_name, avg_metric) in enumerate(sorted_trees):
            if _i == 10:
                break
            print(f"\t\t{_i + 1}. {_tree_name}: {avg_metric:.2f}")

        print("\tWorst 10 trees:")
        for _i, (_tree_name, avg_metric) in enumerate(reversed(sorted_trees)):
            if _i == 10:
                break
            print(f"\t\t{_i + 1}. {_tree_name}: {avg_metric:.2f}")

        print()


def fetch_data_from_table_in_range(table_name, min_count, max_count):
    try:
        last_evaluated_key = None
        data = []

        for _tree_name in selected_tree_names:
            while True:
                if last_evaluated_key:
                    response = dynamodb_client.query(
                        TableName=table_name,
                        KeyConditionExpression='tree_name = :tree_name AND #count BETWEEN :lower AND :upper',
                        ExpressionAttributeNames={
                            '#count': 'count'
                        },
                        ExpressionAttributeValues={
                            ':tree_name': {'S': _tree_name},  # Partition key must be included
                            ':lower': {'N': str(min_count)},  # Sort key range lower bound
                            ':upper': {'N': str(max_count)}  # Sort key range upper bound
                        },
                        ExclusiveStartKey=last_evaluated_key  # For pagination
                    )
                else:
                    response = dynamodb_client.query(
                        TableName=table_name,
                        KeyConditionExpression='tree_name = :tree_name AND #count BETWEEN :lower AND :upper',
                        ExpressionAttributeNames={
                            '#count': 'count'
                        },
                        ExpressionAttributeValues={
                            ':tree_name': {'S': _tree_name},  # Partition key must be included
                            ':lower': {'N': str(min_count)},  # Sort key range lower bound
                            ':upper': {'N': str(max_count)}  # Sort key range upper bound
                        },
                    )

                items = response.get('Items', [])
                for item in items:
                    data.append({
                        'count': int(item['count']['N']),
                        'metric': int(item['metric']['N']),
                        'tree_name': item['tree_name']['S']
                    })

                # Check if there are more items to retrieve
                last_evaluated_key = response.get('LastEvaluatedKey')
                if not last_evaluated_key:
                    break  # No more items to retrieve

        return data

    except Exception as e:
        print(f"Error querying data from table {table_name}: {e}")
        return []


def resolve_tree_color(tree_name):
    # Default color (in case the name doesn't match any pattern)
    color = "black"

    # Miami-style neon colors for "b+" trees
    if "b+" in tree_name:
        order = int(tree_name.split("|o:")[1])
        # Darker, fluorescent yellow scaling to a richer tone
        brightness = min(1.0, max(0.0, float(order - 100) / 200))
        color = (0.9 * brightness, 0.9 * brightness, 0.0 + 0.4 * brightness)  # Darker yellow with richer neon

    # Miami-style neon for "wgb" trees
    elif "wgb" in tree_name:
        parts = tree_name.split("[")
        g_mers_dec = parts[1].replace(']', '')
        order = int(parts[2].split('-')[1][:-1])
        ranking = int(parts[3].split('-')[1][:-1])

        # Random darker neon base color for g_mers_dec
        random.seed(hash(g_mers_dec))
        base_color = [random.random() * 0.6 for _ in range(3)]  # Scaling down for darker tones

        # Adjust the blue and red channels based on order and ranking for richer tones
        base_color[2] = min(1.0, max(0.0, base_color[2] + float(order) / 400))  # Richer blue
        base_color[0] = min(1.0, max(0.0, base_color[0] + float(ranking) / 100))  # Richer red

        color = tuple(base_color)

    # Miami-style cyan for "RB" trees
    elif "RB" in tree_name:
        color = (0.0, 0.8, 0.8)  # Richer, darker cyan

    return color


def plot_graph_range(min_count, max_count):
    for table in tables:
        data = fetch_data_from_table_in_range(table, min_count, max_count)

        # Group data by tree_name
        grouped_data = {}
        for point in data:
            if point['tree_name'] not in grouped_data:
                grouped_data[point['tree_name']] = {'count': [], 'metric': []}
            grouped_data[point['tree_name']]['count'].append(point['count'])
            grouped_data[point['tree_name']]['metric'].append(point['metric'])

        plt.figure(figsize=(20, 12), facecolor='black')  # Larger window with black background
        ax = plt.gca()
        ax.set_facecolor('black')

        for _tree_name, points in grouped_data.items():
            color = resolve_tree_color(_tree_name)
            zorder = 1 if "wgb" in _tree_name else 2
            plt.plot(points['count'], points['metric'], label=_tree_name, color=color, linewidth=3, zorder=zorder)

            plt.text(points['count'][-1], points['metric'][-1], _tree_name, color=color, fontsize=12, va='center',
                     ha='left')

        plt.title(f'{table.capitalize()} Operation Metrics', color='white')  # Title in white
        plt.xlabel('Count', color='white')  # X-axis label in white
        plt.ylabel('Metric', color='white')  # Y-axis label in white

        # Adjust the graph position to provide space for text labels on the right
        plt.subplots_adjust(left=0.1, right=0.9)  # Move the graph to the left

        plt.grid(True, color='gray', linestyle='--', alpha=0.6)  # Subdued grid lines

        # Minimalistic approach by removing top and right borders
        ax.spines['right'].set_visible(False)
        ax.spines['top'].set_visible(False)
        ax.spines['left'].set_color('white')
        ax.spines['bottom'].set_color('white')
        ax.tick_params(axis='x', colors='white')  # X-axis ticks in white
        ax.tick_params(axis='y', colors='white')  # Y-axis ticks in white

        plt.show()


print(" Fetching trees...")
tree_names = fetch_all_tree_names()
selected_tree_names = set()

while True:
    selected_tree_names.clear()

    for idx, tree_name in enumerate(tree_names):
        print(f"\t{idx + 1}. {tree_name}")

    while True:

        if len(selected_tree_names) == 0:
            print("No trees selected. Type 'add <tree_name>' to add a tree to the selection.")
        else:
            print("Selected trees:")
            for idx, tree_name in enumerate(selected_tree_names):
                print(f"\t{idx + 1}. {tree_name}")

        command = input(" > ")

        if command == "exit":
            exit(0)
        elif command.startswith("add"):
            selection = command.split("add ")[1]

            if selection == "*":
                selected_tree_names = set(tree_names)
            else:
                for tree_name in tree_names:
                    if selection in tree_name:
                        selected_tree_names.add(tree_name)
        elif command.startswith("rm"):
            selection = command.split("rm ")[1]

            if selection == "*":
                selected_tree_names.clear()
            else:
                for tree_name in tree_names:
                    if selection in tree_name:
                        selected_tree_names.remove(tree_name)
        elif command == "list":
            for idx, tree_name in enumerate(selected_tree_names):
                print(f"\t{idx + 1}. {tree_name}")
        elif command == "plot":
            break
        elif command == "help":
            print("Commands:")
            print("\tadd <tree_name>: Add a tree to the selection")
            print("\trm <tree_name>: Remove a tree from the selection")
            print("\texit: Exit the program")
            continue
        else:
            print("Invalid command. Type 'help' for a list of commands.")
            continue

    selected_tree_names = list(selected_tree_names)

    print("Fetching data and plotting graphs...")
    print_tree_ranking(0, 10_000)
    plot_graph_range(0, 10_000)

    print_tree_ranking(10_000, 100_000)
    plot_graph_range(10_000, 100_000)

    print_tree_ranking(100_000, 1_000_000)
    plot_graph_range(100_000, 1_000_000)

    print_tree_ranking(0, 1000000)
    plot_graph_range(0, 1000000)

