#include <sys/types.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <netdb.h>
#include <stdio.h>


//Daemon
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <syslog.h>


//Crypto
#include <openssl/bio.h>
#include <openssl/evp.h>
#include <openssl/des.h>

void error(const char *msg)
{
    perror(msg);
    exit(0);
}

void demonize()
{
    //Set our Logging Mask and open the Log
    setlogmask(LOG_UPTO(LOG_NOTICE));
    openlog("wifither", LOG_CONS | LOG_NDELAY | LOG_PERROR | LOG_PID, LOG_USER);

    syslog(LOG_INFO, "Entering Daemon");

    pid_t pid;

    //Fork the Process making a child
    pid = fork();

    //Failed to fork: EXIT_FAILURE
    if (pid < 0) {
        exit(EXIT_FAILURE);
    }

    //We got a good pid, Close the Parent Process
    if (pid > 0) {
        exit(EXIT_SUCCESS);
    }

    //Change File Mask
    umask(0);

    //Create a new Signature Id for our child
    if (setsid() < 0) {
        exit(EXIT_FAILURE);
    }

    //Fork again to remove leadership given by setsid()
    pid = fork();

    //Failed to fork: EXIT_FAILURE
    if (pid < 0) {
        exit(EXIT_FAILURE);
    }

    //We got a good pid, Close the Parent Process
    if (pid > 0) {
        exit(EXIT_SUCCESS);
    }

    //Change the working Directory
    //If we cant find the directory we exit with failure.
    if ((chdir("/")) < 0) {
        exit(EXIT_FAILURE);
    }

    //Close Standard File Descriptors
    close(STDIN_FILENO);
    close(STDOUT_FILENO);
    close(STDERR_FILENO);
}

char *unbase64(unsigned char *input, int length)
{
    BIO *b64, *bmem;

    char *buffer = (char *)malloc(length);
    memset(buffer, 0, length);

    b64 = BIO_new(BIO_f_base64());
    bmem = BIO_new_mem_buf(input, length);
    bmem = BIO_push(b64, bmem);

    BIO_read(bmem, buffer, length);

    BIO_free_all(bmem);

    return buffer;
}

void process(char *port, char *pass)
{
    int sock, length, n;
    socklen_t fromlen;
    struct sockaddr_in server;
    struct sockaddr_in from;
    char buf[1024];
    char command[1024];
    char mac[18] = {0};

    DES_cblock key;
    DES_key_schedule schedule;
    unsigned char *data;

    FILE *fp;
  	char filter[5];

    sock=socket(AF_INET, SOCK_DGRAM, 0);
    if (sock < 0) error("Opening socket");
    length = sizeof(server);
    bzero(&server,length);
    server.sin_family=AF_INET;
    server.sin_addr.s_addr=INADDR_ANY;
    server.sin_port=htons(atoi(port));
    if (bind(sock,(struct sockaddr *)&server,length)<0)
        error("binding");
    fromlen = sizeof(struct sockaddr_in);
    while (1) {
	    memset(buf, 0, sizeof(buf));
        memset(command, 0, sizeof(command));
        n = recvfrom(sock,buf,1024,0,(struct sockaddr *)&from,&fromlen);
        if (n < 0) error("recvfrom");
        data = unbase64(buf, strlen(buf));
        DES_set_key_unchecked((DES_cblock*) pass, &schedule);

        /*
        * Decrypt loop.
        * Does the same, but in a less efficient way than the long version below
        *
        for(int i = 0; i < 17; i += 8) {
            DES_ecb_encrypt((DES_cblock*) (data+i), (DES_cblock*) (data+i), &schedule, DES_DECRYPT);
        }
        */

        DES_ecb_encrypt((DES_cblock*) data, (DES_cblock*) data, &schedule, DES_DECRYPT);
        data += 8;
        DES_ecb_encrypt((DES_cblock*) data, (DES_cblock*) data, &schedule, DES_DECRYPT);
        data += 8;
        DES_ecb_encrypt((DES_cblock*) data, (DES_cblock*) data, &schedule, DES_DECRYPT);
        data -= 16;
        switch(data[0]) {
            case '0':
                system("wifi reload");
                break;

            case '1':
                system("wifi down");
                break;

            case '2':
                system("wifi");
                break;

            case '3':
                system("sed -i '/macfilter/d' /etc/config/wireless");
                break;

            case '4':
                //If macfilter exists
                //Sustitute old value with the new one (deny)
                //Otherwise add a new line with it
                system("sed -i '/option macfilter/{h;s/'\\''.*'\\''/'\\''deny'\\''/};${x;/^$/{s//\\toption macfilter '\\''deny'\\'' /;H};x}' /etc/config/wireless");
                break;

            case '5':
                //If macfilter exists
                //Sustitute old value with the new one (allow)
                //Otherwise add a new line with it
                system("sed -i '/option macfilter/{h;s/'\\''.*'\\''/'\\''allow'\\''/};${x;/^$/{s//\\toption macfilter '\\''allow'\\'' /;H};x}' /etc/config/wireless");
                break;

            case '6':
                strncpy(mac, data+1, 17);
                snprintf(command, sizeof(command), "sed -i '/list maclist '\\''%s'\\''/{h};${x;/^$/{s//\\tlist maclist '\\''%s'\\'' /;H};x}' /etc/config/wireless", mac, mac);
                system(command);
                memset(mac, 0, sizeof(memset));
                break;

            case '7':
                strncpy(mac, data+1, 17);
                snprintf(command, sizeof(command), "sed -i '/%s/d}' /etc/config/wireless", mac, mac);
                system(command);
                memset(mac, 0, sizeof(memset));
                break;

            case '8':
                /* Open the command for reading. */
                fp = popen("sed -n -r 's/.*option macfilter '\\''(allow|deny)'\\''.*/\\1/p' /etc/config/wireless", "r");
                if (fp == NULL) {
                    printf("ERROR: Failed to fetch data\n" );
                }

                /* Read the output a line at a time - output it. */
                if(fgets(filter, 5, fp) == NULL) {
                    n = sendto(sock, "0", 1, 0,(struct sockaddr *)&from, fromlen);
                    if (n  < 0) error("sendto");
                }
                else {
                    if(strcmp(filter, "allo") == 0){
                        n = sendto(sock, "2", 1, 0,(struct sockaddr *)&from, fromlen);
                        if (n  < 0) error("sendto");

                    }
                    else if (strcmp(filter, "deny") == 0){
                        n = sendto(sock, "1", 1, 0,(struct sockaddr *)&from, fromlen);
                        if (n  < 0) error("sendto");
                    }
                }

                /* Close stream */
                pclose(fp);
                break;

            case '9':
                /* Open the command for reading. */
                fp = popen("sed -n -r 's/.*list maclist '\\''(([0-9A-Fa-f]{2}:){5}([0-9A-Fa-f]{2}))'\\''.*/\\1/p' /etc/config/wireless", "r");
                if (fp == NULL) {
                    printf("ERROR: Failed to fetch data\n" );
                }

                /* Read the output a line at a time - output it. */
                while (fgets(mac, 18, fp) != NULL) {
                    n = sendto(sock, &mac, strlen(mac), 0,(struct sockaddr *)&from, fromlen);
                    if (n  < 0) error("sendto");
                    memset(mac, 0, sizeof(mac));
                }
                n = sendto(sock, "done", 4, 0,(struct sockaddr *)&from, fromlen);
                if (n  < 0) error("sendto");

                /* close */
                pclose(fp);
                break;
        }

        n = sendto(sock, &data[0], 1, 0,(struct sockaddr *)&from, fromlen);
        if (n  < 0) error("sendto");
    }
}

int main(int argc, char *argv[]) {

    if (argc < 3) {
        fprintf(stderr,"ERROR: Missing argument\nSintax: wifither [port] [password]\n");
        exit(1);
    }

    if (strlen(argv[2]) < 8) {
        fprintf(stderr,"ERROR: Password must be at leas 8 characters\n");
        exit(1);
    }

    demonize();

    //----------------
    //Main Process
    //----------------
    process(argv[1], argv[2]);    //Run our Process

    //Close the log
    closelog ();

}
