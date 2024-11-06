eval "$(minikube docker-env)"

docker build -f ./iot-management-device/Dockerfile-iot -t iot-management-device:v1 ./iot-management-device
docker build -f ./gateway/Dockerfile-gateway -t gateway:v1 ./gateway

minikube addons enable ingress

kubectl apply -f k8s/mongo-secret.yaml
kubectl apply -f k8s/mongo-configmap.yaml
kubectl apply -f k8s/nats-configmap.yaml

kubectl apply -f k8s/mongo-persistent-volume.yaml
kubectl apply -f k8s/mongo-persistent-volume-claim.yaml
kubectl apply -f k8s/nats-server-deployment.yaml
kubectl apply -f k8s/mongo-deployment.yaml

kubectl wait --for=condition=ready pod -l app=mongodb --timeout=300s

kubectl apply -f k8s/mongo-express-deployment.yaml
kubectl apply -f k8s/iot-management-device-deployment.yaml
kubectl apply -f k8s/gateway-deployment.yaml

kubectl apply -f k8s/ingress.yaml

kubectl wait --for=condition=ready pod --all --timeout=300s
