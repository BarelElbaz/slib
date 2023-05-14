def call(){
    pipeline{
        agent any

        tools{
            go '1.20.1'
        }

        stages{
            stage("Build"){
                steps{
                    log.debug("Building go app")
                    goBuild("bcli")
                }
            }
            stage("Test"){
                steps{
                    goTest()
                }
            }
        }
    }
    
}