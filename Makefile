all: build run

build:
	javac src/*.java

run:
	java -cp src/ Main