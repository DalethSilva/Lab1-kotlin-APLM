package ao.isptec.hello

import java.time.LocalDateTime
import java.util.UUID

class GestorFinanceiro {
    private val utilizadores = mutableListOf<Utilizador>()
    private var sessaoAtual: Sessao? = null
    private var valorMinimo = 0.0

    fun login(email: String, password: String) {
        var utilizador = utilizadores.find { it.email == email }
        if (utilizador == null) {
            utilizador = Utilizador(email, password)
            utilizadores.add(utilizador)
            println("Novo utilizador registado com sucesso!")
        } else {
            if (utilizador.password != password) {
                println("Erro: Password incorreta.")
                return
            }
        }
        val token = UUID.randomUUID().toString()
        sessaoAtual = Sessao(token, LocalDateTime.now(), utilizador)
        println("Sessao iniciada. Token: $token")
    }

    fun logout() {
        if (sessaoAtual != null) {
            sessaoAtual = null
            println("Sessao encerrada com sucesso.")
        } else {
            println("Nenhuma sessao ativa.")
        }
    }

    private fun getMovimentos(): MutableList<Movimento>? {
        return sessaoAtual?.utilizador?.movimentos
    }

    private fun verificarSessao(): Boolean {
        if (sessaoAtual == null) {
            println("Erro: Tem de iniciar sessao primeiro. Use o comando 'u email password'.")
            return false
        }
        return true
    }

    // --- NOVA FUNÇÃO AUXILIAR: Calcula o balanço acumulado até uma data ---
    private fun calcularBalancoAcumulado(dataLimite: String): Double {
        val listaMovimentos = getMovimentos() ?: return 0.0
        var total = 0.0

        // Vamos converter a data limite para o formato de ordenação (AAAA-MM-DD)
        val partesLimite = dataLimite.split("-")
        val dataLimiteOrdenavel = if (partesLimite.size == 3) "${partesLimite[2]}-${partesLimite[1]}-${partesLimite[0]}" else dataLimite

        for (mov in listaMovimentos) {
            val partesMov = mov.data.split("-")
            val dataMovOrdenavel = if (partesMov.size == 3) "${partesMov[2]}-${partesMov[1]}-${partesMov[0]}" else mov.data

            // Se a data do movimento for menor ou igual à data limite
            if (dataMovOrdenavel <= dataLimiteOrdenavel) {
                if (mov.tipo.lowercase() == "receita") total += mov.montante
                else if (mov.tipo.lowercase() == "despesa") total -= mov.montante
            }
        }
        return total
    }

    fun inserir() {
        if (!verificarSessao()) return
        val listaMovimentos = getMovimentos() ?: return

        println("--- Inserir Movimento ---")
        print("Montante: ")
        val montanteInput = readln().replace(",", ".")
        val montante = montanteInput.toDoubleOrNull()

        if (montante == null) {
            println("Erro: Montante invalido.")
            return
        }

        print("Data (ex: 20-10-2026): ")
        val data = readln()
        print("Periodicidade (ex: mensal, unica): ")
        val periodicidade = readln()
        print("Tipo (receita/despesa): ")
        val tipo = readln()
        print("Descricao: ")
        val descricao = readln()

        val novoMovimento = Movimento(montante, data, periodicidade, tipo, descricao)
        listaMovimentos.add(novoMovimento)
        println("Movimento inserido com sucesso!")
    }

    fun listar() {
        if (!verificarSessao()) return
        val listaMovimentos = getMovimentos() ?: return

        println("--- Listar Movimentos ---")
        print("Introduza a data a pesquisar: ")
        val dataPesquisa = readln().trim()

        var encontrou = false
        for (mov in listaMovimentos) {
            if (mov.data == dataPesquisa) {
                println("Montante: ${mov.montante} | Tipo: ${mov.tipo} | Descricao: ${mov.descricao}")
                encontrou = true
            }
        }
        if (!encontrou) println("Nenhum movimento encontrado.")
    }

    fun remover() {
        if (!verificarSessao()) return
        val listaMovimentos = getMovimentos() ?: return

        println("--- Remover Movimento ---")
        print("Data do movimento: ")
        val dataPesquisa = readln().trim()
        print("Descricao do movimento: ")
        val descricaoPesquisa = readln().trim()

        var encontrou = false
        for (i in listaMovimentos.indices) {
            val mov = listaMovimentos[i]
            if (mov.data == dataPesquisa && mov.descricao == descricaoPesquisa) {
                listaMovimentos.removeAt(i)
                println("Movimento removido com sucesso!")
                encontrou = true
                break
            }
        }
        if (!encontrou) println("Erro: Movimento nao encontrado.")
    }

    // --- ATUALIZADA: Usa o balanço acumulado ---
    fun balanco() {
        if (!verificarSessao()) return

        println("--- Calcular Balanco ---")
        print("Data a calcular: ")
        val dataPesquisa = readln().trim()

        val balancoFinal = calcularBalancoAcumulado(dataPesquisa)
        println("Balanco acumulado para a data $dataPesquisa: $balancoFinal")
    }

    fun setValorMinimo() {
        if (!verificarSessao()) return
        println("--- Alterar Valor Minimo ---")
        print("Novo valor minimo: ")
        val input = readln().replace(",", ".")
        val novoValor = input.toDoubleOrNull()

        if (novoValor != null) {
            valorMinimo = novoValor
            println("Valor minimo atualizado para: $valorMinimo")
        } else {
            println("Erro: Valor invalido.")
        }
    }

    // --- ATUALIZADA: Usa o balanço acumulado ---
    fun verificar() {
        if (!verificarSessao()) return
        val listaMovimentos = getMovimentos() ?: return

        println("--- Verificar Validade do Orcamento ---")
        if (listaMovimentos.isEmpty()) {
            println("Orcamento valido (nao existem movimentos).")
            return
        }

        val datasUnicas = mutableSetOf<String>()
        for (mov in listaMovimentos) datasUnicas.add(mov.data)

        var orcamentoValido = true
        for (data in datasUnicas) {
            val totalAcumulado = calcularBalancoAcumulado(data)
            if (totalAcumulado < valorMinimo) {
                println("ALERTA: Balanco acumulado na data $data e $totalAcumulado (inferior ao minimo $valorMinimo).")
                orcamentoValido = false
            }
        }
        if (orcamentoValido) println("O orcamento e VALIDO.")
        else println("O orcamento e INVALIDO.")
    }

    // --- ATUALIZADA: Usa o balanço acumulado ---
    fun comportavel() {
        if (!verificarSessao()) return
        val listaMovimentos = getMovimentos() ?: return

        println("--- Verificar Despesa Comportavel ---")
        print("Valor da despesa: ")
        val valorInput = readln().replace(",", ".")
        val valorDespesa = valorInput.toDoubleOrNull()

        if (valorDespesa == null) {
            println("Erro: Valor invalido.")
            return
        }

        if (listaMovimentos.isEmpty()) {
            println("Sem movimentos registados.")
            return
        }

        val datasUnicas = mutableSetOf<String>()
        for (mov in listaMovimentos) datasUnicas.add(mov.data)

        val datasOrdenadas = datasUnicas.toList().sortedBy { data ->
            val partes = data.split("-")
            if (partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}" else data
        }

        var encontrouData = false

        for (data in datasOrdenadas) {
            val balancoAtual = calcularBalancoAcumulado(data)

            if (balancoAtual - valorDespesa >= valorMinimo) {
                println("Torna-se comportavel na data: $data (Balanco acumulado: $balancoAtual)")
                encontrouData = true
                break
            }
        }
        if (!encontrouData) println("A despesa NAO e comportavel em nenhuma data.")
    }
}