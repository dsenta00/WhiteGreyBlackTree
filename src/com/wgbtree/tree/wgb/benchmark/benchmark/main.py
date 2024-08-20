import boto3
import matplotlib.pyplot as plt

# Initialize DynamoDB client
dynamodb_client = boto3.client(
    'dynamodb',
    region_name='us-west-2',
    endpoint_url='http://localhost:8001',
    aws_access_key_id='FAKE',
    aws_secret_access_key='FAKE'
)


# Fetch data from the table
def fetch_data_from_table(table_name):
    try:
        response = dynamodb_client.scan(TableName=table_name)
        items = response.get('Items', [])
        data = []
        for item in items:
            data.append({
                'count': int(item['count']['N']),
                'metric': int(item['metric']['N']),
                'tree_name': item['tree_name']['S']
            })
        # write into console all tree names
        print(f"Tree names in table {table_name}: {set([point['tree_name'] for point in data])}")
        return data
    except Exception as e:
        print(f"Error fetching data from table {table_name}: {e}")
        return []


# Plot graph
def plot_graph():
    tables = ["insert", "search", "searchMin", "searchMax", "depth"]
    colors = ["red", "blue", "green", "orange", "purple"]

    for table in tables:
        data = fetch_data_from_table(table)

        # Group data by tree_name
        grouped_data = {}
        for point in data:
            if point['tree_name'] not in grouped_data:
                grouped_data[point['tree_name']] = {'count': [], 'metric': []}
            grouped_data[point['tree_name']]['count'].append(point['count'])
            grouped_data[point['tree_name']]['metric'].append(point['metric'])

        # Plot the data
        plt.figure(figsize=(12, 8))
        for idx, (tree_name, points) in enumerate(grouped_data.items()):
            plt.plot(points['count'], points['metric'], label=tree_name, color=colors[idx % len(colors)])

        plt.title(f'{table.capitalize()} Operation Metrics')
        plt.xlabel('Count')
        plt.ylabel('Metric')
        plt.legend(title='Tree Name')
        plt.grid(True)
        plt.show()


plot_graph()
