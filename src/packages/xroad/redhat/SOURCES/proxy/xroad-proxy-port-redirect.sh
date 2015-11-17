#!/bin/bash
if [ "$DISABLE_PORT_REDIRECT" != "" ];
then
    exit 0
fi

declare -A config;

read_config () {
    while IFS='=' read var val 
    do 
        if [[ $var == \[*] ]] 
        then 
            declare section=$var; 
            section=${section#[}; section=${section%]}
        elif [[ $val && ! $var == \;* ]]
        then 
            config["$section.$var"]=$val; 
        fi 
    done < $1
}

read_config "/etc/xroad/conf.d/proxy.ini"
read_config "/etc/xroad/conf.d/local.ini"

HTTP_PORT=${config[proxy.client-http-port]:-0}
HTTPS_PORT=${config[proxy.client-https-port]:-0}

IFACE_PARAM=""
if [ "$NETWORK_INTERFACE" != "" ]
then
    IFACE_PARAM="-i $NETWORK_INTERFACE"
fi

if [ "$1" == "enable" ]
then
    CMD="-I"
    POS=1
else
    CMD="-D"
    POS=
fi

if [ "$HTTPS_PORT" != "0" ]; then
    iptables $CMD PREROUTING $POS -t nat $IFACE_PARAM -p tcp --dport 443 -j REDIRECT --to-port $HTTPS_PORT
    iptables $CMD PREROUTING $POS -t mangle $IFACE_PARAM -p tcp --dport $HTTPS_PORT -j MARK --set-mark 456
fi

if [ "$HTTP_PORT" != "0" ]; then
    iptables $CMD PREROUTING $POS -t nat $IFACE_PARAM -p tcp --dport 80 -j REDIRECT --to-port $HTTP_PORT
    iptables $CMD PREROUTING $POS -t mangle $IFACE_PARAM -p tcp --dport $HTTP_PORT -j MARK --set-mark 456
fi

iptables $CMD INPUT $POS -m mark --mark 456 -j DROP

