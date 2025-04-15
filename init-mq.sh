# Créer le queue manager QM1 en mode silencieux (-q)
echo "Création du queue manager QM1..."
/opt/mqm/bin/crtmqm -q QM1

# Démarrer le queue manager QM1
echo "Démarrage du queue manager QM1..."
/opt/mqm/bin/strmqm QM1

# Modifier la configuration pour désactiver l'authentification
echo "Modification de la configuration de sécurité..."
echo "ALTER QMGR CONNAUTH('')" | runmqsc QM1
echo "ALTER CHANNEL(DEV.APP.SVRCONN) CHLTYPE(SVRCONN) MCAUSER('mqm')" | runmqsc QM1
echo "SET CHLAUTH(DEV.APP.SVRCONN) TYPE(BLOCKUSER) USERLIST('nobody')" | runmqsc QM1
echo "SET CHLAUTH(DEV.APP.SVRCONN) TYPE(ADDRESSMAP) ADDRESS('*') USERSRC(MAP) MCAUSER('mqm') ACTION(REPLACE)" | runmqsc QM1
echo "REFRESH SECURITY TYPE(CONNAUTH)" | runmqsc QM1
echo "REFRESH SECURITY TYPE(CHLAUTH)" | runmqsc QM1

# Garder le container actif
tail -f /dev/null