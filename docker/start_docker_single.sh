cd ../$1
mvn clean package -DskipTests
cd ..
cp -f $1/target/$1-0.0.1-SNAPSHOT.jar docker/$1/$1.jar
cd docker
echo "package done"
docker-compose stop $1
docker-compose up $1