
def call(String binaryName){
    sh "go build -o $binaryName"
}
