#!/bin/bash

# ========================================
# Script de tests API Network Service
# ========================================

BASE_URL="http://localhost:8083"
echo "🚀 Tests API Network Service"
echo "Base URL: $BASE_URL"
echo "========================================"

# Couleurs pour l'affichage
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction pour afficher les résultats
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ SUCCÈS${NC}: $2"
    else
        echo -e "${RED}✗ ÉCHEC${NC}: $2"
    fi
}

echo ""
echo "========================================"
echo "📋 TEST 1: Health Check"
echo "========================================"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/network/health")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" = "200" ]; then
    print_result 0 "Service réseau accessible"
    echo "$body" | jq '.'
else
    print_result 1 "Service réseau non accessible (HTTP $http_code)"
fi

echo ""
echo "========================================"
echo "📋 TEST 2: Statistiques Réseau"
echo "========================================"
response=$(curl -s "$BASE_URL/api/network/stats")
echo "$response" | jq '.'

echo ""
echo "========================================"
echo "📋 TEST 3: Enregistrement d'un nœud User Service"
echo "========================================"
node_response=$(curl -s -X POST "$BASE_URL/api/network/register" \
  -H "Content-Type: application/json" \
  -d '{
    "nodeName": "User Service Test",
    "nodeType": "USER_SERVICE",
    "ipAddress": "localhost",
    "port": 8081,
    "version": "1.0.0",
    "region": "test-region",
    "isValidator": false
  }')

echo "$node_response" | jq '.'
user_node_id=$(echo "$node_response" | jq -r '.node.nodeId')
print_result 0 "Nœud User Service enregistré (ID: $user_node_id)"

echo ""
echo "========================================"
echo "📋 TEST 4: Enregistrement d'un nœud Wallet Service"
echo "========================================"
wallet_response=$(curl -s -X POST "$BASE_URL/api/network/register" \
  -H "Content-Type: application/json" \
  -d '{
    "nodeName": "Wallet Service Test",
    "nodeType": "WALLET_SERVICE",
    "ipAddress": "localhost",
    "port": 8084,
    "version": "1.0.0",
    "region": "test-region",
    "isValidator": false
  }')

echo "$wallet_response" | jq '.'
wallet_node_id=$(echo "$wallet_response" | jq -r '.node.nodeId')
print_result 0 "Nœud Wallet Service enregistré (ID: $wallet_node_id)"

echo ""
echo "========================================"
echo "📋 TEST 5: Enregistrement d'un validateur"
echo "========================================"
validator_response=$(curl -s -X POST "$BASE_URL/api/network/register" \
  -H "Content-Type: application/json" \
  -d '{
    "nodeName": "Validator Test Node",
    "nodeType": "VALIDATOR_NODE",
    "ipAddress": "192.168.1.100",
    "port": 9090,
    "version": "1.0.0",
    "region": "us-east",
    "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA",
    "isValidator": true
  }')

echo "$validator_response" | jq '.'
validator_node_id=$(echo "$validator_response" | jq -r '.node.nodeId')
print_result 0 "Nœud validateur enregistré (ID: $validator_node_id)"

echo ""
echo "========================================"
echo "📋 TEST 6: Récupération des nœuds actifs"
echo "========================================"
active_nodes=$(curl -s "$BASE_URL/api/network/nodes/active")
echo "$active_nodes" | jq '.'
node_count=$(echo "$active_nodes" | jq 'length')
print_result 0 "Nombre de nœuds actifs: $node_count"

echo ""
echo "========================================"
echo "📋 TEST 7: Récupération par type (USER_SERVICE)"
echo "========================================"
user_nodes=$(curl -s "$BASE_URL/api/network/nodes/type/USER_SERVICE")
echo "$user_nodes" | jq '.'

echo ""
echo "========================================"
echo "📋 TEST 8: Récupération des validateurs"
echo "========================================"
validators=$(curl -s "$BASE_URL/api/network/nodes/validators")
echo "$validators" | jq '.'
validator_count=$(echo "$validators" | jq 'length')
print_result 0 "Nombre de validateurs: $validator_count"

echo ""
echo "========================================"
echo "📋 TEST 9: Envoi de heartbeat"
echo "========================================"
if [ ! -z "$user_node_id" ] && [ "$user_node_id" != "null" ]; then
    heartbeat_response=$(curl -s -X POST "$BASE_URL/api/network/heartbeat/$user_node_id")
    echo "$heartbeat_response" | jq '.'
    print_result 0 "Heartbeat envoyé pour le nœud $user_node_id"
else
    print_result 1 "Impossible d'envoyer le heartbeat (nodeId invalide)"
fi

echo ""
echo "========================================"
echo "📋 TEST 10: Envoi d'un message"
echo "========================================"
if [ ! -z "$wallet_node_id" ] && [ "$wallet_node_id" != "null" ]; then
    message_response=$(curl -s -X POST "$BASE_URL/api/network/message/send" \
      -H "Content-Type: application/json" \
      -d "{
        \"messageType\": \"SERVICE_REQUEST\",
        \"sourceNodeId\": \"$user_node_id\",
        \"targetNodeId\": \"$wallet_node_id\",
        \"payload\": \"{\\\"action\\\":\\\"getBalance\\\",\\\"userId\\\":123}\",
        \"priority\": \"NORMAL\",
        \"isBroadcast\": false
      }")
    echo "$message_response" | jq '.'
    print_result 0 "Message envoyé de $user_node_id vers $wallet_node_id"
else
    print_result 1 "Impossible d'envoyer le message (nodeId invalide)"
fi

echo ""
echo "========================================"
echo "📋 TEST 11: Vérification de santé manuelle"
echo "========================================"
health_check_response=$(curl -s -X POST "$BASE_URL/api/network/health-check")
echo "$health_check_response" | jq '.'

echo ""
echo "========================================"
echo "📋 TEST 12: Configuration des nœuds de test"
echo "========================================"
setup_response=$(curl -s -X POST "$BASE_URL/api/network/test/setup/nodes")
echo "$setup_response" | jq '.'

echo ""
echo "========================================"
echo "📋 TEST 13: Scénario de messagerie complet"
echo "========================================"
messaging_scenario=$(curl -s -X POST "$BASE_URL/api/network/test/scenario/messaging")
echo "$messaging_scenario" | jq '.'

echo ""
echo "========================================"
echo "📋 TEST 14: Scénario de heartbeat"
echo "========================================"
heartbeat_scenario=$(curl -s -X POST "$BASE_URL/api/network/test/scenario/heartbeat")
echo "$heartbeat_scenario" | jq '.'

echo ""
echo "========================================"
echo "📋 TEST 15: Statistiques détaillées"
echo "========================================"
detailed_stats=$(curl -s "$BASE_URL/api/network/test/stats/detailed")
echo "$detailed_stats" | jq '.'

echo ""
echo "========================================"
echo "📋 TEST 16: Résumé du réseau"
echo "========================================"
summary=$(curl -s "$BASE_URL/api/network/test/summary")
echo "$summary" | jq '.'

echo ""
echo "========================================"
echo "📋 TEST 17: Désenregistrement d'un nœud"
echo "========================================"
if [ ! -z "$user_node_id" ] && [ "$user_node_id" != "null" ]; then
    dereg_response=$(curl -s -X DELETE "$BASE_URL/api/network/deregister/$user_node_id")
    echo "$dereg_response" | jq '.'
    print_result 0 "Nœud $user_node_id désenregistré"
else
    print_result 1 "Impossible de désenregistrer (nodeId invalide)"
fi

echo ""
echo "========================================"
echo "📋 TEST 18: Nettoyage des nœuds de test"
echo "========================================"
cleanup_response=$(curl -s -X DELETE "$BASE_URL/api/network/test/cleanup/test-nodes")
echo "$cleanup_response" | jq '.'

echo ""
echo "========================================"
echo "📋 TEST 19: Statistiques finales"
echo "========================================"
final_stats=$(curl -s "$BASE_URL/api/network/stats")
echo "$final_stats" | jq '.'

echo ""
echo "========================================"
echo "✅ Tests terminés!"
echo "========================================"