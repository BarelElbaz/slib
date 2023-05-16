package org.develeap
import groovy.json.JsonOutput


@Singleton
class JobData implements Serializable{


    def builtServices = [:]

    @Override
    String toString(){
        return """{
            "builtServices" : ${JsonOutput.toJson(this.builtServices)}
        }"""
    }


}