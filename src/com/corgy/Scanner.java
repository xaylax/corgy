package com.corgy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.corgy.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    // help the scanner keep track of where it is in the source code.
    private int start = 0; // first character in the lexeme being scanned
    private int current = 0; // points at the character currently being looked at
    private int line = 1; // tracks what source line current is on so we can produce tokens that know their location

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {

        //store the raw source code as a simple string, and we have a list ready to fill with tokens we’re going to generate.
        // each turn of the loop, a single token is scanned
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }
        // scanner works its way through the source code, adding tokens until it runs out of characters, then appends one final EOF token.
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    // several lexemes are only a single character in corgy so, all we need to do is consume the next character and pick a token type for it.
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            default:
                Corgy.error(line, "Ruh roh! Unexpected character.");
                break;
    // NOTE: erroneous char is still consumed by the earlier call to advance(). Gotta do that or else we get stuck in an infinite loop.
        }
    }

    // like a conditional advance(). We only consume the current character if it's what we're looking for.
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    // helper method that tells us if we've consumed all the characters
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // helper method that consumes the next character in the source file and returns it
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    // helper method does what advance() does, but just for output
    // grabs the text of the current lexeme and creates a new token for it
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // overload of the method above to handle tokens with literal values
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

}