#include <sys/types.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <netdb.h>
#include <stdio.h>


//Demon
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <syslog.h>


void error(const char *msg)
{
    perror(msg);
    exit(0);
}

void process(char *port){
    int sock, length, n;
    socklen_t fromlen;
    struct sockaddr_in server;
    struct sockaddr_in from;
    char buf[1024];

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
        n = recvfrom(sock,buf,1024,0,(struct sockaddr *)&from,&fromlen);
        if (n < 0) error("recvfrom");
        switch(atoi(buf)) {
            case 0:	
                system("echo 0 > /sys/class/leds/VH4032N:blue:voice/brightness");
                break;
            case 1:
                system("echo 1 > /sys/class/leds/VH4032N:blue:voice/brightness");
                break;
        }       
        
        n = sendto(sock,"Got your message\n",17,
                    0,(struct sockaddr *)&from,fromlen);
        if (n  < 0) error("sendto");
    }
}   

int main(int argc, char *argv[]) {

    if (argc < 2) {
        fprintf(stderr,"ERROR, no port provided\n");
        exit(1);
    }

    //Set our Logging Mask and open the Log
    setlogmask(LOG_UPTO(LOG_NOTICE));
    openlog("wifither", LOG_CONS | LOG_NDELAY | LOG_PERROR | LOG_PID, LOG_USER);

    syslog(LOG_INFO, "Entering Daemon");

    pid_t pid, sid;

   //Fork the Parent Process
    pid = fork();

    if (pid < 0) { exit(EXIT_FAILURE); }

    //We got a good pid, Close the Parent Process
    if (pid > 0) { exit(EXIT_SUCCESS); }

    //Change File Mask
    umask(0);

    //Create a new Signature Id for our child
    sid = setsid();
    if (sid < 0) { exit(EXIT_FAILURE); }

    //Change Directory
    //If we cant find the directory we exit with failure.
    if ((chdir("/")) < 0) { exit(EXIT_FAILURE); }

    //Close Standard File Descriptors
    close(STDIN_FILENO);
    close(STDOUT_FILENO);
    close(STDERR_FILENO);

    //----------------
    //Main Process
    //----------------
    process(argv[1]);    //Run our Process

    //Close the log
    closelog ();
}