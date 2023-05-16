@Library('golib@main')_


def setParameters(){
    properties([
        parameters([
            [$class: 'DynamicReferenceParameter', 
                choiceType: 'ET_FORMATTED_HTML',
                omitValueField: true,
                description: 'list of services to build',
                name: 'BUILD_SERVICES',
                script: [$class: 'GroovyScript',
                    fallbackScript: [
                        classpath: [], 
                        sandbox: true, 
                        script: 'return ["ERROR"]'
                    ],
                    script: [
                        classpath: [], 
                        sandbox: false, 
                        script: """
                            import groovy.json.JsonSlurper
                            import java.net.URL
                            try{
                                def html =  \"\"\"
                                    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
                                    <script src="https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.6/js/standalone/selectize.min.js" integrity="sha256-+C0A5Ilqmu4QcSPxrlGpaZxJ04VjsRjKu+G82kl5UJk=" crossorigin="anonymous"></script>
                                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.6/css/selectize.bootstrap3.min.css" integrity="sha256-ze/OEYGcFbPRmvCnrSeKbRTtjG4vGLHXgOqsyLFTRjg=" crossorigin="anonymous" />
                                    <script>
                                        \\\$( document ).ready(function() {
                                            \\\$('#services2build option').mousedown(function(e) {
                                                e.preventDefault();
                                                var originalScrollTop = \\\$(this).parent().scrollTop();
                                                console.log(originalScrollTop);
                                                \\\$(this).prop('selected', \\\$(this).prop('selected') ? false : true);
                                                var self = this;
                                                \\\$(this).parent().focus();
                                                setTimeout(function() {
                                                    \\\$(self).parent().scrollTop(originalScrollTop);
                                                }, 0);
                                                
                                                return false;
                                            });
                                            \\\$('select').selectize({
                                                sortField: 'text'
                                            });
                                        });
                                    </script>
                                    <select name="value" size="15" multiple="multiple" id="services2build" placeholder="Pick a service...">
                                \"\"\"
                                for(service in ['backend', 'frontend', 'collector']){
                                    html += \"\"\"
                                        <option>\$service</option>
                                    \"\"\"
                                }
                                html += '</select>'
                                return html
                            }catch(e){
                                return [e.toString()]
                            }
                        """.stripIndent()
                    ]
                ]
            ]
        ])
    ])
}

def call(def args = [:]){
    args.body = args.body ?: '${SCRIPT, template="feedback.template"}'
    args.to = args.to ?: 'jenkins.elbaz@gmail.com'
    args.subject = args.subject ?: "Jenkins email report ${env.BUILD_NUMBER}"
    emailext body: "${args.body}",
        subject: "${args.subject}",
        mimeType: 'text/html', //important
        to: "${args.to}",
        saveOutput: true
}

package org.develeap

class Logger implements Serializable {

    static Map colors = [ //ANSI escape sequences
        "RESET" : "\u001B[0m",//turn color back to normal
        "BLACK" : "\u001B[1;30m",
        "RED" : "\u001B[1;31m",
        "GREEN" : "\u001B[1;32m",
        "YELLOW" : "\u001B[1;33m",
        "BLUE" : "\u001B[1;34m",
        "PURPLE" : "\u001B[1;35m"
    ]

    static void printError(steps, String message) {
        if(steps.env.TERM)//check if inside ansiColor scope (TERM is env var setted by AnsiColor plugin)
            steps.echo "${colors.RED}[ERROR] ${message} ${colors.RESET}"
        else
            steps.echo " [ERROR] ${message} "
    }
}

class GoHelper implements Serializable {
    
    def steps
    String goVersion

    def GoHelper(steps, String goVersion){
        this.steps = steps
        this.goVersion = goVersion
    }
}

class Constants{
    static final bcliBinaryName = "bcli"
}

import groovy.json.JsonOutput

static def serializeObj(def obj){
    return JsonOutput.toJson(obj)
}

@Singleton
class JobData implements Serializable{
    public String goVersion = ""

    @Override
    String toString() {
        return """{
                    "goVersion" : "${this.goVersion}", 
                    "pipelineType" : "${this.pipelineType}", 
                    "debPackages" : ${Utils.serializeObj(this.debPackages)}, 
                    }"""
    }
}

class Utils{

    static def getCoresNum(steps){
        return steps.sh(returnStdout: true, script: "nproc --all").trim().toInteger()
    }
}

def msgTemplate = libraryResource("reports/slack_report.template") //load the template

JobData.instance.goVersion