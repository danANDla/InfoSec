#include <cstdint>
#include <iostream>
#include <string>
#include <vector>
#include <cmath>
#include <algorithm>
#include <queue>
#include <set>
#include <map>
#include <iostream>
#include <vector>
#include <string>
#include <set>
#include <algorithm>
#include <ctime>
#include <stack>
#include <iomanip>
#include <unordered_set>
#include <ctime>
#include <cassert>
#include <random>
#include <chrono>

using namespace std;

void solve(string, string);

int main(int argc, char* argv[]){
    solve(argv[1], argv[2]);

}

class Encryptor {        
  public:          
    int n;  
    uint8_t x;  
    uint8_t y;      
    vector<uint8_t> sblock;

    Encryptor(vector<uint8_t> key, int blockSz) {
        n = blockSz;
        x = 0;
        y = 0;
        buildSubstitutionBlock(key);
    }

    vector<uint8_t> encrpyt(vector<uint8_t> msg){

        vector<uint8_t> res;
        for(int i = 0; i < msg.size(); ++i){
            res.push_back(msg[i] ^ generateKey());
        }
        return res;
    }

  private:
    void buildSubstitutionBlock(vector<uint8_t> key){
        for(int i = 0; i < 256; ++i) {
            sblock.push_back(i);
        }

        int j = 0;
        for(int i = 0; i < 256; ++i){
            j = (j + sblock[i] + key[i % key.size()]) % 256;

            uint8_t t = sblock[i];
            sblock[i] = sblock[j];
            sblock[j] = t;
        }
    }

    uint8_t generateKey(){
        x = (x + 1) % 256;
        y = (y + sblock[x]) % 256;

        uint8_t t = sblock[x];
        sblock[x] = sblock[y];
        sblock[y] = t;

        return sblock[(sblock[x] + sblock[y]) % 256];
    }

};

void solve(string msg, string key){
    vector<uint8_t> key_bytes(key.begin(), key.end());
    
    Encryptor rc4(key_bytes, 8);

    vector<uint8_t> msg_bytes(msg.begin(), msg.end());
    vector<uint8_t> encr = rc4.encrpyt(msg_bytes);
    cout << "encrpyted msg: ";
    for(int i = 0; i < encr.size(); ++i) printf("%c", encr[i]);
    printf("\n");

    Encryptor rc4_decr(key_bytes, 8);
    vector<uint8_t> decr = rc4_decr.encrpyt(encr);
    cout << "decrpyted msg: ";
    for(int i = 0; i < decr.size(); ++i) printf("%c", decr[i]);
    printf("\n");
}
