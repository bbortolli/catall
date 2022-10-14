# Descrição geral

Catálogo geral de estabelecimentos, sem envolvimento de transações de qualquer tipo, apenas listagem e amostra de maneira adequada.

# Definições de modelagem
Objetos que fazem parte do sistema

1. Estabelecimento:
  - id
  - endereço
  - categoria[]
  - nome
  - descrição
  - cardapio[]

2. Categoria:
  - id
  - descricao

3. Cardapio:
  - id
  - nome

# Docker

docker run -it --name catall -p 3000:3000  -v %cd%:/api -w /api clojure:latest bash
