import json
import os
def lambda_handler(event, context):
    json_region = os.environ['AWS_REGION']
    print(event)
    return {
        "StatusCode": 200,
        "headers": {
            "Content-Type": "application/json"
        },
        "body": json.dumps({
            "message ": "Received confirmed onsite visit event"
        })
    }