import org.develeap.Constants

def call(String binaryName = Constants.binaryName){
    sh "go build -o $binaryName"
    sh """#!/usr/bin/env python3
import datetime

def foo()

    """
}
