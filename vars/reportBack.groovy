def call(def args = [:]){
    args.to = args.to ?: "jenkins.elbaz@gmail.com"

    emailext body: '${SCRIPT, template="feedback.template"}',
             to: args.to,
             subject: 'Jenkins report',
             mimeType: 'text/html',


}

