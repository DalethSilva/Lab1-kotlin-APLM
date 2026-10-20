package ao.isptec.hello

// Data class para representar um movimento financeiro
data class Movimento(
    val montante: Double,
    val data: String,
    val periodicidade: String,
    val tipo: String,
    val descricao: String
)