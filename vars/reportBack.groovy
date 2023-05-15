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