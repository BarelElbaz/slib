def call(){
    def template = libraryResource('slack_template.txt')
    template.replace('<DEV>', "Barel")
    template.replace('<STATUS>', "PASS")
}