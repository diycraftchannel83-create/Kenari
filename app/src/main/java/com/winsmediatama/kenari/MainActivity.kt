package com.winsmediatama.kenari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Forest = Color(0xFF173D32)
private val ForestLight = Color(0xFF315B4B)
private val Gold = Color(0xFFE2C36B)
private val Ivory = Color(0xFFF5F4ED)
private val Ink = Color(0xFF1D2A24)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KenariApp() }
    }
}

@Composable
private fun KenariApp() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Forest, secondary = Gold, background = Ivory,
            surface = Color.White, onPrimary = Color.White, onBackground = Ink
        )
    ) {
        var selected by remember { mutableStateOf(0) }
        Scaffold(
            containerColor = Ivory,
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    val items = listOf("Beranda", "Kenari", "Geneologi", "Breeding", "Lainnya")
                    val icons = listOf(Icons.Default.Home, Icons.Default.Pets, Icons.Default.AccountTree, Icons.Default.Favorite, Icons.Default.Menu)
                    items.forEachIndexed { index, label ->
                        NavigationBarItem(
                            selected = selected == index,
                            onClick = { selected = index },
                            icon = { Icon(icons[index], contentDescription = label) },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Forest,
                                selectedTextColor = Forest,
                                indicatorColor = Color(0xFFE4EDE6)
                            )
                        )
                    }
                }
            },
            floatingActionButton = {
                if (selected == 1) FloatingActionButton(
                    onClick = { },
                    containerColor = Forest,
                    contentColor = Color.White
                ) { Icon(Icons.Default.Add, "Tambah kenari") }
            }
        ) { padding ->
            when (selected) {
                0 -> Dashboard(Modifier.padding(padding))
                1 -> PlaceholderPage("Data Kenari", "Database kenari akan tersedia di modul CRUD.")
                2 -> PlaceholderPage("Geneologi", "Silsilah multi-generasi akan ditampilkan di sini.")
                3 -> PlaceholderPage("Breeding", "Pasangan dan catatan breeding.")
                else -> PlaceholderPage("Menu Lainnya", "Jenis kenari • Laporan • Backup • Pengaturan")
            }
        }
    }
}

@Composable
private fun Dashboard(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().background(Ivory).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("KENARI TASIKMALAYA", color = Forest, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.5.sp)
                Text("Selamat datang, Breeder", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Kelola koleksi dan silsilah kenari Anda", color = ForestLight, fontSize = 13.sp)
            }
            Surface(shape = RoundedCornerShape(16.dp), color = Forest, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Pets, null, tint = Gold, modifier = Modifier.size(27.dp))
                }
            }
        }
        Surface(shape = RoundedCornerShape(24.dp), color = Forest, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("RINGKASAN KOLEKSI", color = Gold, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.2.sp)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("0", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                    Text("  kenari terdaftar", color = Color.White.copy(alpha = .75f), modifier = Modifier.padding(bottom = 8.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryChip("Jantan", "0")
                    SummaryChip("Betina", "0")
                    SummaryChip("Breeding", "0")
                }
            }
        }
        Text("Akses Cepat", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAction("Tambah Kenari", Icons.Default.AddCircle, Modifier.weight(1f))
            QuickAction("Geneologi", Icons.Default.AccountTree, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAction("Pasangan", Icons.Default.Favorite, Modifier.weight(1f))
            QuickAction("Laporan", Icons.Default.Assessment, Modifier.weight(1f))
        }
        Surface(color = Color.White, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Mulai membangun silsilah", fontWeight = FontWeight.Bold, color = Forest)
                Spacer(Modifier.height(6.dp))
                Text("Tambahkan kenari pertama Anda. Foto, kode ring, tanggal menetas, dan data indukan akan menjadi dasar geneologi.", color = ForestLight, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun SummaryChip(label: String, count: String) {
    Surface(color = Color.White.copy(alpha = .12f), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(count, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(label, color = Color.White.copy(alpha = .75f), fontSize = 10.sp)
        }
    }
}

@Composable
private fun QuickAction(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Surface(modifier, color = Color.White, shape = RoundedCornerShape(18.dp), onClick = {}) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, contentDescription = label, tint = Forest, modifier = Modifier.size(28.dp))
            Text(label, fontWeight = FontWeight.SemiBold, color = Ink, fontSize = 13.sp)
        }
    }
}

@Composable
private fun PlaceholderPage(title: String, description: String) {
    Column(Modifier.fillMaxSize().background(Ivory).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Forest)
        Text(description, color = ForestLight)
        Surface(color = Color.White, shape = RoundedCornerShape(20.dp)) {
            Text("Modul sedang dalam tahap pengembangan.", Modifier.padding(18.dp), color = Ink)
        }
    }
}
