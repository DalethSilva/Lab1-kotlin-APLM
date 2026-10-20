package ao.isptec.hello

fun main() {
    // Instanciar o gestor (o cérebro do sistema)
    val gestor = GestorFinanceiro()
    var cmd: String

    while (true) {
        print("> ")
        cmd = readln().trim()

        // O comando 'u' tem parâmetros, por isso precisa de um tratamento antes do 'when'
        if (cmd.startsWith("u ")) {
            // Divide o comando por espaços: ["u", "email", "password"]
            val partes = cmd.split(" ")

            if (partes.size == 3) {
                gestor.login(partes[1], partes[2])
            } else {
                println("Erro: Formato correto e 'u email password'.")
            }
            continue // Volta ao início do ciclo para pedir o próximo comando
        }

        val comandoLimpo = cmd.lowercase()

        // Identifica o comando e chama o método correspondente no gestor
        when (comandoLimpo) {
            "inserir", "i" -> gestor.inserir()
            "listar", "l" -> gestor.listar()
            "remover", "r" -> gestor.remover()
            "balanco", "b" -> gestor.balanco()
            "m" -> gestor.setValorMinimo()
            "v" -> gestor.verificar()
            "w" -> gestor.comportavel()
            "exit" -> gestor.logout()
            "sair" -> {
                println("A sair do programa...")
                break // Encerra o programa
            }
            else -> println("Comando nao reconhecido: $cmd")
        }
    }
}