package br.com.teadescomplica

import android.app.Application
import br.com.teadescomplica.core.di.DiModule

/**
 * Classe de aplicação do TEA Descomplica.
 *
 * Inicializa o módulo de injeção de dependências. Não faz nenhuma rede nem
 * coleta de dados.
 */
class TeaDescomplicaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DiModule.init(this)
    }
}
