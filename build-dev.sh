mkdir -p /tmp/build/dist/bin && mkdir -p /tmp/build/dist/config
mvn -f ./frontend/pom.xml clean compile \
    && mvn -f ./backend/pom.xml clean package \
    && unzip ./backend/target/backend*.jar -d ./dist/ \
    && cp ./backend/src/main/resources/application.properties ./dist/config/
test "$?" != 0 && exit 1
cd ./dist/ && tar -czf ../target.tar.gz .
