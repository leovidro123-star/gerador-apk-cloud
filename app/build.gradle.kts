package com.gerador.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class GeneratedFile(
    val path: String,
    val code: String
)

data class GeneratedApp(
    val appName: String,
    val appType: String,
    val architectureLearned: String,
    val setupInstructions: String,
    val files: List<GeneratedFile>,
    val timestamp: String
)

data class KnowledgeItem(
    val id: String,
    val category: String,
    val content: String,
    val date: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF6C63FF),
                    onPrimary = Color.White,
                    secondary = Color(0xFF03DAC6),
                    background = Color(0xFF12131C),
                    surface = Color(0xFF1E1F2E),
                    surfaceVariant = Color(0xFF28293D),
                    onSurface = Color(0xFFEDEDED),
                    onSurfaceVariant = Color(0xFFAAAAAA)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppGeneratorDashboard()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppGeneratorDashboard() {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }

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
    var learnEnabled by remember { mutableStateOf(true) }

    // Memória Neural (Simulada para visualização e aprendizado contínuo)
    val neuralMemory = remember {
        mutableStateListOf(
            KnowledgeItem(
                id = "1",
                category = "Clean Architecture Jetpack Compose",
                content = "Camadas: UI (Compose), StateHolder (ViewModel), Repository e UseCases com Coroutines.",
                date = "2025-01-15"
            ),
            KnowledgeItem(
                id = "2",
                category = "FastAPI + JWT Auth",
                content = "Padrão OAuth2PasswordBearer, Bcrypt password hashing e validações via Pydantic Schemas.",
                date = "2025-02-10"
            )
        )
    }

    // Histórico de Apps Gerados
    val generatedApps = remember { mutableStateListOf<GeneratedApp>() }

    // Estados de Geração
    var promptInput by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var generationStep by remember { mutableStateOf("") }
    var currentAppResult by remember { mutableStateOf<GeneratedApp?>(null) }

    // Diálogos e Modais
    var showTeachDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00FF7F))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Meta-Gerador de Apps",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    AssistChip(
                        onClick = { selectedTab = 1 },
                        label = { Text("Memória: ${neuralMemory.size}") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Gerador") },
                    icon = { Text("⚡", fontSize = 18.sp) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Memória Neural") },
                    icon = { Text("🧠", fontSize = 18.sp) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Projetos (${generatedApps.size})") },
                    icon = { Text("📁", fontSize = 18.sp) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    label = { Text("Configurações") },
                    icon = { Text("⚙️", fontSize = 18.sp) }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> GeneratorScreen(
                    selectedModel = selectedModel,
                    prompt = promptInput,
                    onPromptChange = { promptInput = it },
                    isGenerating = isGenerating,
                    currentStep = generationStep,
                    appResult = currentAppResult,
                    onGenerate = {
                        if (promptInput.isNotBlank()) {
                            coroutineScope.launch {
                                isGenerating = true
                                generationStep = "Consultando base vetorial ChromaDB..."
                                delay(1200)

                                generationStep = "Roteando para $selectedModel..."
                                delay(1500)

                                generationStep = "Projetando arquitetura e compilando arquivos..."
                                delay(1800)

                                val appSlug = promptInput.take(15).lowercase().replace(" ", "_")
                                val newApp = GeneratedApp(
                                    appName = "app_$appSlug",
                                    appType = if (promptInput.contains("web", true)) "Fullstack React+FastAPI" else "Android Jetpack Compose",
                                    architectureLearned = "Padrão modular com injeção de dependência simplificada, tratamento de estados via StateFlow e validação de schema estrito em: $promptInput",
                                    setupInstructions = "./gradlew assembleDebug\nou\npip install -r requirements.txt && uvicorn main:app --reload",
                                    files = listOf(
                                        GeneratedFile("MainActivity.kt", "// Código gerado por $selectedModel\nfun main() {\n    println(\"Projeto rodando com sucesso!\")\n}"),
                                        GeneratedFile("build.gradle.kts", "plugins {\n    id(\"com.android.application\")\n}"),
                                        GeneratedFile("README.md", "# Projeto gerado com aprendizado de RAG Evolutivo\nPrompt: $promptInput")
                                    ),
                                    timestamp = "Agora"
                                )

                                if (learnEnabled) {
                                    neuralMemory.add(
                                        KnowledgeItem(
                                            id = (neuralMemory.size + 1).toString(),
                                            category = newApp.appType,
                                            content = newApp.architectureLearned,
                                            date = "Agora"
                                        )
                                    )
                                }

                                generatedApps.add(0, newApp)
                                currentAppResult = newApp
                                isGenerating = false
                            }
                        }
                    }
                )

                1 -> MemoryScreen(
                    memories = neuralMemory,
                    onAddKnowledge = { showTeachDialog = true }
                )

                2 -> ProjectsScreen(apps = generatedApps)

                3 -> SettingsScreen(
                    models = aiModels,
                    selectedModel = selectedModel,
                    onModelSelect = { selectedModel = it },
                    apiKey = apiKey,
                    onKeyChange = { apiKey = it },
                    learnMode = learnEnabled,
                    onLearnToggle = { learnEnabled = it }
                )
            }

            if (showTeachDialog) {
                TeachMemoryDialog(
                    onDismiss = { showTeachDialog = false },
                    onSave = { category, content ->
                        neuralMemory.add(
                            KnowledgeItem(
                                id = (neuralMemory.size + 1).toString(),
                                category = category,
                                content = content,
                                date = "Manual"
                            )
                        )
                        showTeachDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun GeneratorScreen(
    selectedModel: String,
    prompt: String,
    onPromptChange: (String) -> Unit,
    isGenerating: Boolean,
    currentStep: String,
    appResult: GeneratedApp?,
    onGenerate: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Motor Ativo", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(selectedModel, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                    Text("Multi-IA RAG", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        item {
            OutlinedTextField(
                value = prompt,
                onValueChange = onPromptChange,
                label = { Text("Descreva o aplicativo completo") },
                placeholder = { Text("Ex: Sistema ERP com autenticação JWT, backend FastAPI e frontend Compose...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isGenerating
            )
        }

        item {
            Button(
                onClick = onGenerate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isGenerating && prompt.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("🚀 Gerar Aplicativo Completo", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isGenerating) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(currentStep, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }

        if (appResult != null && !isGenerating) {
            item {
                Text("Resultado da Geração", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Projeto: ${appResult.appName}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Categoria: ${appResult.appType}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Instruções de Inicialização:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Surface(
                            color = Color.Black.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = appResult.setupInstructions,
                                modifier = Modifier.padding(8.dp),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            items(appResult.files) { file ->
                FilePreviewItem(file = file)
            }
        }
    }
}

@Composable
fun FilePreviewItem(file: GeneratedFile) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📄 ${file.path}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(if (expanded) "▲" else "▼", fontSize = 12.sp)
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = file.code,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp),
                            color = Color(0xFF00FF7F)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryScreen(
    memories: List<KnowledgeItem>,
    onAddKnowledge: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Memória Neural Evolutiva", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("ChromaDB / Padrões Aprendidos: ${memories.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = onAddKnowledge, shape = RoundedCornerShape(8.dp)) {
                Text("Ensinar IA")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(memories) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.category,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 13.sp
                            )
                            Text(item.date, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = item.content, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectsScreen(apps: List<GeneratedApp>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Projetos Gerados", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Repositório local de códigos construídos", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        if (apps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum app construído ainda. Use a aba Gerador!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(apps) { app ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(app.appName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(app.timestamp, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(app.appType, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Arquivos gerados: ${app.files.size}", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    models: List<String>,
    selectedModel: String,
    onModelSelect: (String) -> Unit,
    apiKey: String,
    onKeyChange: (String) -> Unit,
    learnMode: Boolean,
    onLearnToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Configurações Multi-IA", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Selecione o Modelo de Linguagem:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                models.forEach { model ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onModelSelect(model) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (model == selectedModel),
                            onClick = { onModelSelect(model) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(model, fontSize = 13.sp)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Chave de API", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Opcional se utilizar modelo Ollama Local", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = onKeyChange,
                    placeholder = { Text("Ex: sk-proj-... ou AIzaSy...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Auto-Evolução (RAG)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Aprender automaticamente novos padrões com cada app compilado.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = learnMode, onCheckedChange = onLearnToggle)
            }
        }
    }
}

@Composable
fun TeachMemoryDialog(
    onDismiss: () -> Unit,
    onSave: (category: String, content: String) -> Unit
) {
    var category by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Injetar Conhecimento na IA") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoria / Arquitetura") },
                    placeholder = { Text("Ex: Cache com Redis") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Regras e Padrões de Código") },
                    placeholder = { Text("Descreva o padrão ou modelo técnico") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (category.isNotBlank() && content.isNotBlank()) {
                        onSave(category, content)
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}