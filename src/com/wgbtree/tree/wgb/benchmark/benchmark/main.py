import random
from math import log10

import boto3
import matplotlib.pyplot as plt

tables = [
    "insert",
    "search",
    #"searchMin",
    #"searchMax",
    "depth",
    "searchRange"
]

metric_importance = {
    "search": 1,
    "searchRange": 1,
    "depth": 0,
    #"searchMin": 0.6,
    #"searchMax": 0.6,
    "insert": 0.2
}

colors = {
    "blue": [0.0, 0.0, 1.0],
    "green": [0.0, 1.0, 0.0],
    "orange": [1.0, 0.5, 0.0],
    "purple": [0.5, 0.0, 0.5],
    "brown": [0.5, 0.25, 0.0],
    "pink": [1.0, 0.0, 1.0],
    "gray": [0.5, 0.5, 0.5],
    "olive": [0.5, 0.5, 0.0],
    "cyan": [0.0, 1.0, 1.0],
}

dynamodb_client = boto3.client(
    'dynamodb',
    region_name='us-east-2',
    endpoint_url='http://localhost:8001',
    aws_access_key_id='FAKE',
    aws_secret_access_key='FAKE'
)


def fetch_all_tree_names():
    try:
        return fetch_distinct_values("insert", "tree_name")
    except Exception as e:
        print(f"Error fetching tree names: {e}")
        return []


def fetch_distinct_values(table_name, attribute_name):
    paginator = dynamodb_client.get_paginator('scan')
    response_iterator = paginator.paginate(
        TableName=table_name,
        ProjectionExpression=attribute_name
    )

    distinct_values = set()
    for page in response_iterator:
        for item in page['Items']:
            distinct_values.add(item[attribute_name]['S'])  # Assuming attribute type is String (S)

    return list(distinct_values)


def print_stats(_data_per_tables):
    total_ranking_by_tree = {}
    total_ranking_by_tree_charac = {}

    for table, data in _data_per_tables:
        print(f"\t{table.capitalize()}:")

        cumulative_data = {}
        cumulative_data_per_charac = {}

        for point in data:
            _tree_name = point['tree_name']
            count = point['count']
            metric = point['metric']

            if "[o-" in _tree_name:
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
        _min = sorted_trees[0][1]
        longest_tree_name = max([len(_tree_name) for _tree_name, _ in sorted_trees])

        for _i, (_tree_name, avg_metric) in enumerate(sorted_trees):
            if _tree_name not in total_ranking_by_tree_charac:
                total_ranking_by_tree_charac[_tree_name] = 0
            relative_metric = avg_metric / _min
            total_ranking_by_tree_charac[_tree_name] += relative_metric * metric_importance[table]

        for _i, (_tree_name, avg_metric) in enumerate(sorted_trees):
            padding_len = longest_tree_name - len(_tree_name) + 3
            padding = "." * padding_len
            relative_metric = avg_metric / _min

            print(f"\t\t{_i + 1}. {_tree_name} {padding} {avg_metric:.2f} ({relative_metric:.2f})")

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
        _min = sorted_trees[0][1]

        for _i, (_tree_name, avg_metric) in enumerate(sorted_trees):
            if _tree_name not in total_ranking_by_tree:
                total_ranking_by_tree[_tree_name] = 0
            relative_metric = avg_metric / _min
            total_ranking_by_tree[_tree_name] += relative_metric * metric_importance[table]

        longest_tree_name = max([len(_tree_name) for _tree_name, _ in sorted_trees])

        print(f"\tTop 10 trees:")
        for _i, (_tree_name, avg_metric) in enumerate(sorted_trees):
            if _i == 10:
                break

            padding_len = longest_tree_name - len(_tree_name) + 3
            padding = "." * padding_len

            print(f"\t\t{_i + 1}. {_tree_name} {padding} {avg_metric:.2f} ({avg_metric / _min:.2f})")

        print("\tWorst 10 trees:")
        for _i, (_tree_name, avg_metric) in enumerate(reversed(sorted_trees)):
            if _i == 10:
                break

            padding_len = longest_tree_name - len(_tree_name) + 3
            padding = "." * padding_len
            print(f"\t\t{_i + 1}. {_tree_name} {padding} {avg_metric:.2f} ({avg_metric / _min:.2f})")

        print()

    print("\tTotal ranking by tree:")
    sorted_trees = sorted(total_ranking_by_tree.items(), key=lambda x: x[1])
    _min = sorted_trees[0][1]
    longest_tree_name = max([len(_tree_name) for _tree_name, _ in sorted_trees])

    for _i, (_tree_name, avg_metric) in enumerate(sorted_trees):
        padding_len = longest_tree_name - len(_tree_name) + 3
        padding = "." * padding_len
        print(f"\t\t{_i + 1}. {_tree_name} {padding} {avg_metric:.2f} ({avg_metric / _min:.2f})")

    print()

    print("\tTotal ranking by tree characteristic:")
    sorted_trees = sorted(total_ranking_by_tree_charac.items(), key=lambda x: x[1])
    _min = sorted_trees[0][1]
    longest_tree_name = max([len(_tree_name) for _tree_name, _ in sorted_trees])

    for _i, (_tree_name, avg_metric) in enumerate(sorted_trees):
        padding_len = longest_tree_name - len(_tree_name) + 3
        padding = "." * padding_len
        print(f"\t\t{_i + 1}. {_tree_name} {padding} {avg_metric:.2f} ({avg_metric / _min:.2f})")

    print()


def fetch_data_from_table_in_range(table_name, _min_count = None, _max_count = None):
    try:
        print(f" > Fetching {table_name} ...")
        last_evaluated_key = None
        data = []

        for _tree_name in selected_tree_names:
            while True:
                if last_evaluated_key:
                    if _min_count is None and _max_count is None:
                        response = dynamodb_client.query(
                            TableName=table_name,
                            KeyConditionExpression='tree_name = :tree_name',
                            ExpressionAttributeValues={
                                ':tree_name': {'S': _tree_name},
                            },
                            ExclusiveStartKey=last_evaluated_key
                        )
                    else:
                        response = dynamodb_client.query(
                            TableName=table_name,
                            KeyConditionExpression='tree_name = :tree_name AND #count BETWEEN :lower AND :upper',
                            ExpressionAttributeNames={
                                '#count': 'count'
                            },
                            ExpressionAttributeValues={
                                ':tree_name': {'S': _tree_name},
                                ':lower': {'N': str(_min_count)},
                                ':upper': {'N': str(_max_count)}
                            },
                            ExclusiveStartKey=last_evaluated_key
                        )
                else:
                    if _min_count is None and _max_count is None:
                        response = dynamodb_client.query(
                            TableName=table_name,
                            KeyConditionExpression='tree_name = :tree_name',
                            ExpressionAttributeValues={
                                ':tree_name': {'S': _tree_name},
                            }
                        )
                    else:
                        response = dynamodb_client.query(
                            TableName=table_name,
                            KeyConditionExpression='tree_name = :tree_name AND #count BETWEEN :lower AND :upper',
                            ExpressionAttributeNames={
                                '#count': 'count'
                            },
                            ExpressionAttributeValues={
                                ':tree_name': {'S': _tree_name},
                                ':lower': {'N': str(_min_count)},
                                ':upper': {'N': str(_max_count)}
                            },
                        )

                items = response.get('Items', [])
                for item in items:
                    data.append({
                        'count': int(item['count']['N']),
                        'metric': int(item['metric']['N']),
                        'tree_name': item['tree_name']['S']
                    })

                last_evaluated_key = response.get('LastEvaluatedKey')
                if not last_evaluated_key:
                    break

        return data

    except Exception as e:
        print(f"Error querying data from table {table_name}: {e}")
        return []


def resolve_tree_color(_tree_name):
    color = "black"

    if "b+" in _tree_name:
        order = int(_tree_name.split("|o:")[1])
        brightness = min(1.0, max(0.0, float(order - 100) / 200))
        color = (0.9 * brightness, 0.9 * brightness, 0.0 + 0.4 * brightness)  # Darker yellow with richer neon
    elif "wgb" in _tree_name:
        parts = _tree_name.split("[")
        wgb_type = parts[1].replace(']', '')
        order = int(parts[2].split('-')[1][:-1])
        ranking = int(parts[3].split('-')[1][:-1])

        random.seed(wgb_type)
        base_color = colors[random.choice(list(colors.keys()))]
        base_color[0] = min(1.0, max(0.0, (float(ranking) / 8192)))
        base_color[2] = min(1.0, max(0.0, (float(order) / 600)))

        color = tuple(base_color)
    elif "RB" in _tree_name:
        color = "red"

    return color


def plot_graph_range(_data_per_tables):
    for table, data in _data_per_tables:
        grouped_data = {}
        for point in data:
            if point['tree_name'] not in grouped_data:
                grouped_data[point['tree_name']] = {'count': [], 'metric': []}
            grouped_data[point['tree_name']]['count'].append(point['count'])
            grouped_data[point['tree_name']]['metric'].append(point['metric'])

        plt.figure(figsize=(20, 12), facecolor='black')
        ax = plt.gca()
        ax.set_facecolor('black')

        for _tree_name, points in grouped_data.items():
            color = resolve_tree_color(_tree_name)
            zorder = 1 if "wgb" in _tree_name else 2
            plt.plot(points['count'], points['metric'], label=_tree_name, color=color, linewidth=3, zorder=zorder)

            plt.text(points['count'][-1], points['metric'][-1], _tree_name, color=color, fontsize=12, va='center',
                     ha='left')

        plt.title(f'{table.capitalize()} Operation Metrics', color='white')
        plt.xlabel('Count', color='white')
        plt.ylabel('Metric', color='white')

        plt.subplots_adjust(left=0.1, right=0.9)

        plt.grid(True, color='gray', linestyle='--', alpha=0.6)

        ax.spines['right'].set_visible(False)
        ax.spines['top'].set_visible(False)
        ax.spines['left'].set_color('white')
        ax.spines['bottom'].set_color('white')
        ax.tick_params(axis='x', colors='white')
        ax.tick_params(axis='y', colors='white')

        plt.show()


print(" > Fetching trees ...")
tree_names = fetch_all_tree_names()
selected_tree_names = set()
to_print = True

while True:
    selected_tree_names.clear()

    for idx, tree_name in enumerate(tree_names):
        print(f"\t{idx + 1}. {tree_name}")

    while True:

        if to_print is False:
            to_print = True
        elif len(selected_tree_names) == 0:
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
                        try:
                            selected_tree_names.remove(tree_name)
                        except KeyError:
                            pass
        elif command == "list":
            for idx, tree_name in enumerate(selected_tree_names):
                print(f"\t{idx + 1}. {tree_name}")
            to_print = False
        elif command == "plot":
            print("Fetching data and plotting graphs...")
            data_per_tables = [(table, fetch_data_from_table_in_range(table)) for table in tables]
            print_stats(data_per_tables)
            plot_graph_range(data_per_tables)
            to_print = False
        elif command.startswith("plot"):
            print("Fetching data and plotting graphs...")
            selected_tree_names = list(selected_tree_names)
            min_count = int(command.split("plot ")[1].split(" ")[0])
            max_count = int(command.split("plot ")[1].split(" ")[1])
            data_per_tables = [(table, fetch_data_from_table_in_range(table, min_count, max_count)) for table in tables]
            print_stats(data_per_tables)
            plot_graph_range(data_per_tables)
            to_print = False
            continue
        elif command == "help":
            print("Commands:")
            print("\tadd <tree_name>: Add a tree to the selection")
            print("\trm <tree_name>: Remove a tree from the selection")
            print("\texit: Exit the program")
            to_print = False
            continue
        else:
            print("Invalid command. Type 'help' for a list of commands.")
            to_print = False
            continue
