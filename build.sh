export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
nvm install --lts

cd frontend/
npm audit fix && npm i && npm run build

cd ../

mvn clean package

docker build -t profielwerkstuk .