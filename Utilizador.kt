package ao.isptec.hello

import java.time.LocalDateTime

// Classe que representa um utilizador do sistema
data class Utilizador(
    val email: String,
    val password: String,
    // Cada utilizador tem a sua própria lista de movimentos
    val movimentos: MutableList<Movimento> = mutableListOf()
)

// Classe que representa a sessão ativa de um utilizador
data class Sessao(
    val token: String, // O hash único gerado no login
    val dataHora: LocalDateTime, // O momento exato em que o login foi feito
    val utilizador: Utilizador // Quem é o dono desta sessão
)