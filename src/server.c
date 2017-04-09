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

void process(char *port)
{
    int sock, length, n;
    socklen_t fromlen;
    struct sockaddr_in server;
    struct sockaddr_in from;
    char buf[1024];
    char command[1024];

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
        switch(atoi(buf)) {
            case 1:
                //system("wifi off");
                system("echo 0 > /sys/class/leds/VH4032N:blue:voice/brightness");
                break;

            case 2:
                //system("wifi on");
                system("echo 1 > /sys/class/leds/VH4032N:blue:voice/brightness");
                break;

            case 3:
                system("sed -i '/maclist\\|macfilter/d' /etc/config/wireless");
                break;

            case 4:
                //If macfilter exists
                //Sustitute old value with the new one (deny)
                //Otherwise add a new line with it
                system("sed -i '/option macfilter/{h;s/'\\''.*'\\''/'\\''deny'\\''/};${x;/^$/{s//\\toption macfilter '\\''deny'\\'' /;H};x}' /etc/config/wireless");
                break;

            case 5:
                //If macfilter exists
                //Sustitute old value with the new one (allow)
                //Otherwise add a new line with it
                system("sed -i '/option macfilter/{h;s/'\\''.*'\\''/'\\''allow'\\''/};${x;/^$/{s//\\toption macfilter '\\''allow'\\'' /;H};x}' /etc/config/wireless");
                break;

            default:
                snprintf(command, sizeof(command), "sed -i '/list maclist '\\''%s'\\''/{d};${x;/^$/{s//\\tlist maclist '\\''%s'\\'' /;H};x}' /etc/config/wireless", buf, buf);
                system(command);
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

    demonize();

    //----------------
    //Main Process
    //----------------
    process(argv[1]);    //Run our Process

    //Close the log
    closelog ();

}