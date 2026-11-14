package br.com.teadescomplica

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import br.com.teadescomplica.core.locale.AppLanguages
import br.com.teadescomplica.core.navigation.TeaNavigation

/**
 * Atividade principal. Tudo é renderizado com Jetpack Compose.
 *
 * Não solicita permissão de internet e não acessa serviços externos.
 */
class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLanguages.activityContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                TeaNavigation(AppLanguages.selected(this)) { language ->
                    if (AppLanguages.select(this, language)) recreate()
                }
            }
        }
    }
}
