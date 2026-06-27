package weatherapp.data.local

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth // <- Importar Auth
import io.github.jan.supabase.postgrest.Postgrest
import weatherapp.config.AppConfig

val supabaseClient = createSupabaseClient(
    supabaseUrl = AppConfig.SUPABASE_URL,
    supabaseKey = AppConfig.SUPABASE_ANON_KEY
) {
    install(Postgrest)
    install(Auth) // <- Instalar Auth
}