

def call(){
    pipeline{
        agent any

        // tools{
        //     go '1.20.1'
        // }

        stages{
            stage("Build & Push Services"){
                steps{
                    buildServices(["frontend", "backend", "collector"])
                }
            }
        }
    }
    
}