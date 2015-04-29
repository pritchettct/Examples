#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "loader.h"
#include "memory.h"
#include <ctype.h>
/*
 * Loader for Y86 Processor
 *
 * Author: Coley Pritchett
 *
 */


bool load(int argv, char * args[])
{
    FILE * file;
    int lineNumber = 0;
    char input[80];
    bool memError;
    int address = 0;
    int prevAddress = -1;
    if (argv < 2)
    {
        printf("Too few arguments. Usage: yess <filename>.yo\n");
        return false;
    }

    if (validFileName(args[1]))
    {
        file = fopen(args[1], "r");
        if (file == NULL)
        {
            printf("File failed to open. Usage: yess <filename>.yo\n");
            return false;
        }
    }
    else
    {
        printf("File name is invalid. Usage: yess <filename>.yo\n");
        return false;
    }

    while(fgets(input, 80, file))
    {
        lineNumber++;
        int validLine = !checkLine(input);
        if (validLine)
        {
            printf("Error on line %d\n%s\n", lineNumber, input);
            return false;
        }
        else
        {
	        if (isComment(input))
	        {
		    discardRest(input, file);
	    	    continue;
	        }
	        else
            {
                address = grabAddress(input);
                if (address >= prevAddress)
                {
                    loadIntoMemory(input, address, &prevAddress, &memError);
                    if (memError)
                    {
                        printf("Memory Error.\n");
                        return false;
                    }
                }
                else
                {
                    printf("Error on line %d\n%s\n", lineNumber, input);
                    return false;
                }
	        }
        }
    }
    return true;
}

/*
 * validFileName
 * Takes as input a string representing the name of a file and returns TRUE
 * if the string ends with a .yo extension.
 * @param file - pointer to the name of the file
 */
bool validFileName(char * file)
{
    int len = strlen(file);
    return strcmp(&file[len - 3], ".yo") == 0 ? true : false;
}

/*
 * checkLine
 * Checks to see if the line is valid.
 * @param file - file data
 */
bool checkLine(char * file)
{
    if (isSpaces(file, 0, 21))
    {
        return true;
    }
    if (isAddress(file) && file[22] == '|' && file[8] == ' ' && isData(file))
    {
        return true;
    }
    return false;
}
/*
 * grabAddress
 * Takes as input a data record and returns the address in that line.
 * @param file - data file
 */ 
unsigned int grabAddress(char * file)
{
    return strtol(&file[2], NULL, 16);
}

/*
 * isComment
 * Checks to see if the specified line is a comment.
 * @param file - file data
 */
bool isComment(char * file)
{
    return isSpaces(file, 0, 21) || (isAddress(file) && !isData(file));
}

/*
 * loadIntoMemory
 * Loads data into memory
 * @param file - file data
 * @param address - address to use
 * @param lastAddress - pointer to final address
 * @param memError - flag in case of errors
 */
void loadIntoMemory(char * file, unsigned int address, int * prevAddress, bool * memError)
{
    int memIndex = 0;
    int arrayIndex = 9;
    while (arrayIndex < 21 && file[arrayIndex] != ' ' && !*memError)
    {
        putByte(address + memIndex, grabDataByte(file, arrayIndex), memError);
        arrayIndex = arrayIndex + 2;
        memIndex++;
    }
    if (!*memError)
    {
        *prevAddress = address + memIndex;
    }
}

/* discardRest
 * Discards the remainder of a line in the file.
 * @param lastLine - pointer to the last line
 * @param stream - stream to the file
 */
void discardRest(char * lastLine, FILE *stream)
{
    for (int i = 0; i < strlen(lastLine); i++)
    {
        if (lastLine[i] == '\n')
        {
            return;
        }
    }
    while (fgetc(stream) != '\n');
    return;
}

/*
 * isAddress
 * Takes as input a record and returns true if the record contains an address.
 * @param file - file data
 */
bool isAddress(char * file)
{
    if (strncmp(&file[2], "0x", 2))
    {
        return false;
    }
    int isHex = checkHex(file, 4, 6);
    return isHex && (file[7] == ':');
}

/*
 * isData
 * Takes as input a record and returns true if the record contains data to
 * be stored.
 * @param file - file data
 */
bool isData(char * file)
{
    int len = strcspn(&file[9], " ");
    int isHex = checkHex(file, 9, 9 + len - 1);
    if (len % 2 != 0)
    {
        return false;
    }
    return isHex || (len == 0);
}

/*
 * isSpaces
 * Takes as input a record and starting and ending indices into that record
 * and returns true if there are spaces beginning at the starting position
 * through to the ending position.
 * @param file - file data
 * @param start - first character to check
 * @param end - end character to check
 */
bool isSpaces(char * file, int start, int end)
{
    for (int i = start; i <= end; i++)
    {
        if (file[i] != ' ')
        {
            return false;
        }
    }
    return true;
}

/*
 * checkHex
 * Takes as input a record and starting and ending indices into that record
 * and returns true if there are hex characters beginning at the starting
 * position through to the ending position.
 * @param file - file data
 * @param start - first character to check
 * @param end - last character to check
 */
bool checkHex(char * file, int start, int end)
{
    for (int i = start; i <= end; i++)
    {
        if (isxdigit(file[i]) == false)
        {
            return false;
        }
    }
    return true;
}

/*
 * grabDataByte
 * Takes as input a record and starting index into that record and returns
 * the value of the data byte at the position.
 * @param file - file data
 * @param index - starting index
 */
unsigned char grabDataByte(char * file, int index)
{
    char copy[3] = {0, 0, 0};
    memcpy(copy, &file[index], 2);
    return (unsigned char) strtol(copy, NULL, 16);
}
