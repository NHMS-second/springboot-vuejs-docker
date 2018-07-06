export REGISTRY_URL=${YOUR_REGISTRY_URL}
find $WORKSPACE -type d -name ".svn" | xargs rm -rf
docker login -u ${YOUR_REGISTRY_USER} -p ${YOUR_REGISTRY_PASSWORD} $REGISTRY_URL
docker run --rm -a STDOUT -a STDERR -v $WORKSPACE/:/tmp/build/ -w /tmp/build \
	--name ${YOUR_BUILD_CONTAINER_NAME} \
	$REGISTRY_URL/${YOUR_GROUP_NAME}/${YOUR_ENV_IMAGE_NAME}:${YOUR_ENV_IMAGE_TAG} \
	bash build-dev.sh
docker build -t \
	$REGISTRY_URL/${YOUR_GROUP_NAME}/${YOUR_DIST_IMAGE_NAME}:${YOUR_DIST_IMAGE_TAG} \
	-f Dockerfile-dev.dist .
docker push \
	$REGISTRY_URL/${YOUR_GROUP_NAME}/${YOUR_DIST_IMAGE_NAME}:${YOUR_DIST_IMAGE_TAG}
if [ -n "$(docker images -f 'dangling=true' -q)" ]
	then docker rmi $(docker images -f 'dangling=true' -q)
fi
