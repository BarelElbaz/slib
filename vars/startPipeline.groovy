

def call(){
    pipeline{
        agent any

        // tools{
        //     go '1.20.1'
        // }

        stages{
            stage("Build & Push Services"){
                steps{
                    echo " Build & Push Services stage ".center(61, "=")
                    buildServices(["frontend", "backend", "collector"])
                }
            }
        }
    }
    
}