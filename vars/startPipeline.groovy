def call(){
    properties([])
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
        post{
            always{
                script{
                    env.JOB_DATA = '{"builtServices": {"service1": "006262944085.dkr.ecr.us-east-1.amazonaws.com/service1:1", "service2": "006262944085.dkr.ecr.us-east-1.amazonaws.com/service2:1", "service3": "006262944085.dkr.ecr.us-east-1.amazonaws.com/service3:1"}}'
                    reportBack()
                }
            }
        }
    }
    
}