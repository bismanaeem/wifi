//Optional code to improve

// Remove macfilter and add each mac from a comma separated string [problems with the end characters]
char *mac
system("sed -i '/macfilter/d' ./wireless");
mac=strtok(buf,",");
while (mac != NULL) {
    char command[100];
    snprintf(command, sizeof(command), "sed -i -e '$i \\\\tlist maclist '\\''%.*s'\\''' ./wireless", 17, mac);
    system(command);
    //write(1,command,n);
    //write(1,mac,17);
    mac = strtok (NULL, ",");
}

//regex ([0-9A-Fa-f]{2}:){5}([0-9A-Fa-f]{2})
//MACs  "01-00-5e-00-00-02,01-00-5e-00-00-16,01-00-5e-00-00-fb,01-00-5e-00-00-fc,01-00-5e-7f-ff-fa,"


//Make command sed to add a MAC address
snprintf(command, sizeof(command), "sed -i -e '$i \\\\tlist maclist '\\''%.*s'\\''' ./wireless", 17, buf);
case 0:
    write(1,"caso0",5);
    //snprintf(command, sizeof(command), "sed -i -e '$i \\\\tlist maclist '\\''%.*s'\\''' ./wireless", 17, buf);
    snprintf(command, sizeof(command), "sed -i '/list maclist '\\''%s'\\''/{d};${x;/^$/{s//\\tlist maclist '\\''%s'\\'' /;H};x}' ./wireless", buf, buf);
    system(command);
    break;