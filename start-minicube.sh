eval "$(minikube docker-env)"

./gradlew clean build

docker build -t app:latest .

minikube addons enable ingress

kubectl apply -f k8s/mongo-secret.yaml
kubectl apply -f k8s/iot-management-device-configmap.yaml
kubectl apply -f k8s/mongo-persistent-volume.yaml
kubectl apply -f k8s/mongo-persistent-volume-claim.yaml

kubectl apply -f k8s/mongo-deployment.yaml

kubectl wait --for=condition=ready pod -l app=mongodb --timeout=300s

kubectl apply -f k8s/mongo-express-deployment.yaml
kubectl apply -f k8s/iot-management-device-deployment.yaml

kubectl apply -f k8s/ingress.yaml

POD_NAME=$(kubectl get pods -l app=mongodb -o jsonpath='{.items[0].metadata.name}')

kubectl cp init-mongo.sh $POD_NAME:/tmp/init-mongo.sh

kubectl exec -it $POD_NAME -- /bin/sh -c "chmod +x /tmp/init-mongo.sh"

kubectl exec -it $POD_NAME -- /bin/sh -c "/tmp/init-mongo.sh"

kubectl wait --for=condition=ready pod --all --timeout=300s
