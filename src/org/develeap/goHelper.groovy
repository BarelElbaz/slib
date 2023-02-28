package org.develeap

class goHelper{

    String goVersion = ""

    def goHelper(String goVersion){
        this.goVersion = goVersion
    }

    def getGoVersion(){ return this.goVersion; }

}