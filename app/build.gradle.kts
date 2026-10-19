package com.gerador.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// ==========================================
// MODELOS DE DADOS DA ARQUITETURA MULTI-IA
// ==========================================
data class SourceFile(
    val path: String,
    val code: String
)

data class GeneratedProject(
    val id: String = UUID.randomUUID().toString(),
    val appName: String,
    val appType: String,
    val architectureLearned: String,
    val setupInstructions: String,
    val files: List<SourceFile>,
    val timestamp: String,
    val modelUsed: String
)

data class NeuralPattern(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val architectureSummary: String,
    val timestamp: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkColors = darkColorScheme(
                primary = Color(0xFF6366F1),
                secondary = Color(0xFF10B981),
                background = Color(0xFF0F172A),
                surface = Color(0xFF1E293B),
                surfaceVariant = Color(0xFF334155),
                onPrimary = Color.White,
                onSecondary = Color.White,
                onBackground = Color(0xFFF8FAFC),
                onSurface = Color(0xFFF1F5F9),
                onSurfaceVariant = Color(0xFFCBD5E1)
            )

            MaterialTheme(colorScheme = darkColors) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MetaAppBuilderScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaAppBuilderScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Memória Neural Simulada em Tempo Real
    val neuralMemory = remember {
        mutableStateListOf(
            NeuralPattern(
                category = "Autenticação JWT + Clean Architecture",
                architectureSummary = "Separação estrita de camadas: Domain (UseCases), Data (Repository + Retrofit/Ktor), Presentation (Compose MVI). Tokens persistidos com EncryptedSharedPreferences e interceptor de renovação de refresh token.",
                timestamp = "Inicial (Base)"
            ),
            NeuralPattern(
                category = "Offline-First & Room Cache",
                architectureSummary = "Sincronização reativa usando Flow, WorkManager para sincronização de fundo com tratamento de conflitos Last-Write-Wins e fallback local com Room DB.",
                timestamp = "Inicial (Base)"
            ),
            NeuralPattern(
                category = "Fullstack FastAPI + SQLite + JWT",
                architectureSummary = "Backend assíncrono com SQLAlchemy 2.0, Pydantic v2 para validação, endpoints tipados e CORS restrito a origens seguras.",
                timestamp = "Inicial (Base)"
            )
        )
    }

    // Histórico de Apps Gerados
    val generatedHistory = remember { mutableStateListOf<GeneratedProject>() }

    // Configurações Multi-IA
    val aiModels = listOf(
        "gemini/gemini-2.5-pro",
        "claude-3-7-sonnet-latest",
        "gpt-4o",
        "groq/llama-3.3-70b-versatile",
        "ollama/deepseek-coder-v2"
    )
    var selectedModel by remember { mutableStateOf(aiModels[0]) }
    var apiKey by remember { mutableStateOf("") }
    var showApiKey by remember { mutableStateOf(false) }
    var continuousLearningEnabled by remember { mutableStateOf(true) }

    // Estado do Gerador
    var userPrompt by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var generationStage by remember { mutableStateOf("") }
    var currentProject by remember { mutableStateOf<GeneratedProject?>(null) }

    // Abas de Navegação
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("🚀 Construtor", "🧬 Memória Neural", "⚙️ Fine-Tuning & Sandbox")

    // Diálogo Manual de Ensino
    var showTeachDialog by remember { mutableStateOf(false) }
    var teachCategory by remember { mutableStateOf("") }
    var teachContent by remember { mutableStateOf("") }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        scope.launch {
            snackbarHostState.showSnackbar("Copiado para a área de transferência!")
        }
    }

    fun startGeneration() {
        if (userPrompt.trim().isEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar("Digite a descrição do aplicativo primeiro!")
            }
            return
        }

        scope.launch {
            isGenerating = true
            generationStage = "1/4: Consultando banco vetorial de padrões aprendidos..."
            delay(1000)

            generationStage = "2/4: $selectedModel sintetizando arquitetura modular..."
            delay(1200)

            generationStage = "3/4: Codificando componentes e arquivos do projeto..."
            delay(1400)

            val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

            val isMobileTarget = userPrompt.contains("apk", ignoreCase = true) ||
                    userPrompt.contains("android", ignoreCase = true) ||
                    userPrompt.contains("mobile", ignoreCase = true)

            val projectName = if (isMobileTarget) "App_${System.currentTimeMillis().toString().takeLast(4)}" else "Service_${System.currentTimeMillis().toString().takeLast(4)}"
            val projectType = if (isMobileTarget) "Android Nativo (Kotlin + Jetpack Compose)" else "Fullstack Web (FastAPI + React)"

            val generatedFiles = if (isMobileTarget) {
                listOf(
                    SourceFile(
                        path = "app/src/main/AndroidManifest.xml",
                        code = """
                            <?xml version="1.0" encoding="utf-8"?>
                            <manifest xmlns:android="http://schemas.android.com/apk/res/android">
                                <uses-permission android:name="android.permission.INTERNET" />
                                <application
                                    android:label="$projectName"
                                    android:theme="@android:style/Theme.Material.NoActionBar">
                                    <activity android:name=".MainActivity" android:exported="true">
                                        <intent-filter>
                                            <action android:name="android.intent.action.MAIN" />
                                            <category android:name="android.intent.category.LAUNCHER" />
                                        </intent-filter>
                                    </activity>
                                </application>
                            </manifest>
                        """.trimIndent()
                    ),
                    SourceFile(
                        path = "app/build.gradle.kts",
                        code = """
                            plugins {
                                alias(libs.plugins.android.application)
                                alias(libs.plugins.kotlin.android)
                            }
                            android {
                                namespace = "com.gerador.$projectName".lowercase()
                                compileSdk = 35
                                defaultConfig {
                                    minSdk = 26
                                    targetSdk = 35
                                    versionCode = 1
                                    versionName = "1.0.0"
                                }
                            }
                            dependencies {
                                implementation(platform("androidx.compose:compose-bom:2024.09.00"))
                                implementation("androidx.compose.material3:material3")
                            }
                        """.trimIndent()
                    ),
                    SourceFile(
                        path = "app/src/main/java/MainActivity.kt",
                        code = """
                            package com.gerador.$projectName.lowercase()
                            
                            import android.os.Bundle
                            import androidx.activity.ComponentActivity
                            import androidx.activity.compose.setContent
                            import androidx.compose.material3.Text
                            
                            class MainActivity : ComponentActivity() {
                                override fun onCreate(savedInstanceState: Bundle?) {
                                    super.onCreate(savedInstanceState)
                                    setContent {
                                        Text("Projeto $projectName gerado com sucesso pelo Meta-Gerador!")
                                    }
                                }
                            }
                        """.trimIndent()
                    )
                )
            } else {
                listOf(
                    SourceFile(
                        path = "backend/main.py",
                        code = """
                            from fastapi import FastAPI, HTTPException, Depends
                            from pydantic import BaseModel
                            
                            app = FastAPI(title="$projectName API", version="1.0")
                            
                            class ItemSchema(BaseModel):
                                title: str
                                description: str
                                active: bool = True
                            
                            @app.get("/")
                            async def root():
                                return {"status": "online", "system": "$projectName"}
                            
                            @app.post("/items")
                            async def create_item(item: ItemSchema):
                                return {"message": "Item salvo", "data": item}
                        """.trimIndent()
                    ),
                    SourceFile(
                        path = "backend/requirements.txt",
                        code = """
                            fastapi>=0.110.0
                            uvicorn[standard]>=0.28.0
                            pydantic>=2.6.0
                        """.trimIndent()
                    ),
                    SourceFile(
                        path = "run.sh",
                        code = """
                            #!/bin/bash
                            echo "Iniciando $projectName..."
                            python3 -m venv venv
                            source venv/bin/activate
                            pip install -r backend/requirements.txt
                            uvicorn backend.main:app --reload --port 8000
                        """.trimIndent()
                    )
                )
            }

            val newArchitectureInsight = "Padrão de injeção de dependência e desacoplamento gerado para '$userPrompt'. Otimizado para latência reduzida e consumo de memória sob demanda."

            val project = GeneratedProject(
                appName = projectName,
                appType = projectType,
                architectureLearned = newArchitectureInsight,
                setupInstructions = if (isMobileTarget) {
                    "./gradlew assembleDebug\n# O APK gerado estará em: app/build/outputs/apk/debug/app-debug.apk"
                } else {
                    "chmod +x run.sh && ./run.sh\n# Acesse a documentação Swagger em http://localhost:8000/docs"
                },
                files = generatedFiles,
                timestamp = currentDate,
                modelUsed = selectedModel
            )

            generationStage = "4/4: Absorvendo novo aprendizado na rede neural evolutiva..."
            delay(800)

            if (continuousLearningEnabled) {
                neuralMemory.add(
                    0,
                    NeuralPattern(
                        category = projectType,
                        architectureSummary = newArchitectureInsight,
                        timestamp = currentDate
                    )
                )
            }

            generatedHistory.add(0, project)
            currentProject = project
            isGenerating = false
            generationStage = ""

            snackbarHostState.showSnackbar("✅ Projeto '${project.appName}' sintetizado com sucesso!")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🧠 Meta-Gerador de APKs",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF059669))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "MULTI-IA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Seletor de Abas
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Conteúdo de Cada Aba
            when (selectedTab) {
                0 -> BuilderTabContent(
                    models = aiModels,
                    selectedModel = selectedModel,
                    onModelSelect = { selectedModel = it },
                    apiKey = apiKey,
                    onApiKeyChange = { apiKey = it },
                    showApiKey = showApiKey,
                    onToggleShowApiKey = { showApiKey = !showApiKey },
                    continuousLearning = continuousLearningEnabled,
                    onToggleContinuousLearning = { continuousLearningEnabled = it },
                    memoryCount = neuralMemory.size,
                    prompt = userPrompt,
                    onPromptChange = { userPrompt = it },
                    isGenerating = isGenerating,
                    stageMessage = generationStage,
                    onGenerate = { startGeneration() },
                    currentProject = currentProject,
                    onCopyCode = { label, code -> copyToClipboard(label, code) }
                )
                1 -> NeuralMemoryTabContent(
                    patterns = neuralMemory,
                    onTeachClick = { showTeachDialog = true },
                    onDeletePattern = { id -> neuralMemory.removeAll { it.id == id } }
                )
                2 -> FineTuningTabContent(
                    totalLearned = neuralMemory.size,
                    generatedCount = generatedHistory.size,
                    onCopyDatasetMock = {
                        val sample = "{\"prompt\": \"criar tela login\", \"completion\": \"class LoginActivity...\"}"
                        copyToClipboard("Dataset JSONL", sample)
                    }
                )
            }
        }
    }

    // Modal / Dialogo de Aprendizado Manual
    if (showTeachDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showTeachDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "📚 Injetar Conhecimento na IA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Cadastre padrões arquiteturais ou regras críticas que os modelos devem sempre respeitar ao gerar novos apps.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = teachCategory,
                        onValueChange = { teachCategory = it },
                        label = { Text("Categoria ou Padrão") },
                        placeholder = { Text("Ex: Criptografia AES-GCM + Clean Arch") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = teachContent,
                        onValueChange = { teachContent = it },
                        label = { Text("Descrição Técnica / Template") },
                        placeholder = { Text("Descreva regras de pastas, bibliotecas obrigatórias ou estruturas...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 6
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { showTeachDialog = false }) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (teachCategory.isNotBlank() && teachContent.isNotBlank()) {
                                    val now = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                    neuralMemory.add(
                                        0,
                                        NeuralPattern(
                                            category = teachCategory.trim(),
                                            architectureSummary = teachContent.trim(),
                                            timestamp = now
                                        )
                                    )
                                    teachCategory = ""
                                    teachContent = ""
                                    showTeachDialog = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Conhecimento memorizado com sucesso!")
                                    }
                                }
                            }
                        ) {
                            Text("Salvar na Memória")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// ABA 1: CONSTRUTOR & ORQUESTRADOR MULTI-IA
// ==========================================
@Composable
fun BuilderTabContent(
    models: List<String>,
    selectedModel: String,
    onModelSelect: (String) -> Unit,
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    showApiKey: Boolean,
    onToggleShowApiKey: () -> Unit,
    continuousLearning: Boolean,
    onToggleContinuousLearning: (Boolean) -> Unit,
    memoryCount: Int,
    prompt: String,
    onPromptChange: (String) -> Unit,
    isGenerating: Boolean,
    stageMessage: String,
    onGenerate: () -> Unit,
    currentProject: GeneratedProject?,
    onCopyCode: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Seção do Motor IA
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "⚙️ Motor de Inteligência Artificial",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Modelo Ativo:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Chips de Seleção de Modelo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    models.forEach { model ->
                        FilterChip(
                            selected = selectedModel == model,
                            onClick = { onModelSelect(model) },
                            label = {
                                Text(
                                    text = model.substringAfter("/"),
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Campo da Chave de API
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = onApiKeyChange,
                    label = { Text("Chave da API (Vazio para Ollama Local)") },
                    placeholder = { Text("sk-ant-... / AIzaSy... / gsk_...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (showApiKey) "Ocultar" else "Ver",
                            modifier = Modifier
                                .clickable { onToggleShowApiKey() }
                                .padding(8.dp),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                // Switch de Aprendizado Contínuo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontal