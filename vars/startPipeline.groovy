import org.develeap.JobData

def initParameters(){
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


def call(){
    initParameters()
    pipeline{
        agent any

        // tools{
        //     go '1.20.1'
        // }
        
        stages{
            stage("Build & Push Services"){
                steps{
                    echo " Build & Push Services stage ".center(61, "=")
                    buildServices(["collector", "frontend"])
                }
            }
        }
        post{
            always{
                script{
                    env.JOB_DATA = JobData.instance.toString()
                    reportBack()
                }
            }
        }
    }
    
}