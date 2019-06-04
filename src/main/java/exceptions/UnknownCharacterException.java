package main.java.exceptions;

public class UnknownCharacterException extends Exception{

    char unknownCharacter;

    public UnknownCharacterException(char unknownCharacter){
        super();
        this.unknownCharacter=unknownCharacter;
    }

    @Override
    public String toString() {
        return String.format("character %s is not supported",unknownCharacter);
    }
}
