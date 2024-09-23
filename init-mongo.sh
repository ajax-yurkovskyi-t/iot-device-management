MONGO_HOST="localhost"
MONGO_PORT="27017"
MONGO_INITDB_ROOT_USERNAME="taras"
MONGO_INITDB_ROOT_PASSWORD="taras"

DB_NAME="mongo-db"
ROLES='[
  { _id: ObjectId(), roleName: "USER" },
  { _id: ObjectId(), roleName: "ADMIN" }
]'

execute_mongo_command() {
    mongosh mongodb://$MONGO_HOST:$MONGO_PORT -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD --eval "
    db = db.getSiblingDB(\"$DB_NAME\");
    db.role.insertMany($ROLES);
    "
}

check_if_data_exists() {
    mongosh mongodb://$MONGO_HOST:$MONGO_PORT -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD --eval "
    db = db.getSiblingDB(\"$DB_NAME\");
    var roles = db.role.find().toArray();
    if (roles.length > 0) {
        print('Data already exists');
        quit(0);
    } else {
        print('Data does not exist');
        quit(1);
    }
    " > /dev/null
}

max_retries=5
retry_interval=1

echo "Attempting to initialize MongoDB..."

for i in $(seq 1 $max_retries); do
    if check_if_data_exists; then
            echo "Data already seeded. Skipping initialization."
            exit 0
        elif execute_mongo_command; then
            echo "MongoDB initialized successfully."
            exit 0
    else
        echo "MongoDB initialization failed. Retry $i/$max_retries..."
        sleep $retry_interval
    fi
done

echo "MongoDB initialization failed after $max_retries attempts."
exit 1
