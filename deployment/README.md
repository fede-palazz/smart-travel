# Smart Travel Deployment

## Getting started

### Secrets

In order to publish the secrets:

1. Create a `.env` file

2. Fill it with the following secret variables:

    ```properties
    DB_URL="MongoDB connection string"
    PAYPAL_CLIENT_ID="PayPal sandbox client id"
    PAYPAL_CLIENT_SECRET="PayPal sandbox client secret"
    BROKER_USERNAME="RabbitMQ username"
    BROKER_PASSWORD="RabbitMQ password"
    ```
3. Run the following command to create the secrets in okd:

    ```bash
    # Create secret from env file
    oc create secret generic app-secrets --from-env-file=.env

    # Delete secret
    oc delete secret app-secrets
    ```

### ConfigMap

In order to publish the ConfigMap values execute: `oc apply -f configmap.yaml`

To delete it run: `oc delete configmap NAME`

### Deployment

```bash
# Apply the deployment
oc apply -f travel-catalog-deployment.yaml

# Check the deployment status
oc get deployments
oc get pods
oc get services

# Check if the pod is ready
oc describe pod -l app=travel-catalog
```