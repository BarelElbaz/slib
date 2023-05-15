

def call(List services2Build = []){
    def parallelTasks = [:]
    
    services2Build.each{service ->
        stageName = service
        parallelTasks[stageName] = {
            dir(service){
                String imageName = "slib/${service}:${env.BUILD_NUMBER}"
                log.info("Building ${imageName}...")
                docker.withRegistry('https://006262944085.dkr.ecr.us-east-1.amazonaws.com', 'ecr:us-east-1:aws-ecr-private') {
                    def serviceImage = docker.build("$imageName")
                    serviceImage.push()
                    //get the image URL to docker pull from
                    def imageURI = docker.image("$imageName").imageId
                    log.info("$imageURI pushed successfully")
                }
            }
        }
    }
    parallelTasks.failFast = true

    //Dispatch!
    parallel(parallelTasks)
}