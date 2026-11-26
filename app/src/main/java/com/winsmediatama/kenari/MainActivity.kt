package com.winsmediatama.kenari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.winsmediatama.kenari.data.BirdEntity
import com.winsmediatama.kenari.data.KenariViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Forest = Color(0xFF173D32)
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
    val context = LocalContext.current
    val vm = remember { ViewModelProvider(context as ComponentActivity)[KenariViewModel::class.java] }
    val birds by vm.birds.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var edit by remember { mutableStateOf<BirdEntity?>(null) }
    var form by remember { mutableStateOf(false) }
    var delete by remember { mutableStateOf<BirdEntity?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    MaterialTheme(colorScheme = lightColorScheme(primary = Forest, secondary = Gold, background = Ivory, surface = Color.White)) {
        Scaffold(containerColor = Ivory, bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val names = listOf("Beranda", "Kenari", "Geneologi", "Breeding", "Lainnya")
                val icons = listOf(Icons.Default.Home, Icons.Default.Pets, Icons.Default.AccountTree, Icons.Default.Favorite, Icons.Default.Menu)
                names.forEachIndexed { i, n -> NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = { Icon(icons[i], n) }, label = { Text(n, fontSize = 10.sp) }) }
            }
        }, floatingActionButton = { if(tab == 1) FloatingActionButton(onClick = { edit = null; form = true }, containerColor = Forest, contentColor = Color.White) { Icon(Icons.Default.Add, "Tambah") } }) { p ->
            when(tab) {
                0 -> Home(birds, Modifier.padding(p), { edit = null; form = true }, { tab = it })
                1 -> BirdList(birds, Modifier.padding(p), { edit = it; form = true }, { delete = it })
                2 -> GenealogyPage(birds, Modifier.padding(p))
                3 -> BasicPage("Breeding", "Berikutnya: pasangan, periode breeding, telur, inkubasi, dan data anakan.")
                else -> BasicPage("Lainnya", "Katalog jenis • Laporan • Backup/Restore • Pengaturan")
            }
        }
    }
    if(form) BirdForm(edit, birds, { form = false }, { bird -> vm.save(bird) { result -> result.onSuccess { form = false }.onFailure { error = it.message } } })
    delete?.let { b -> AlertDialog(onDismissRequest = { delete = null }, title = { Text("Hapus kenari?") }, text = { Text("Hapus data " + b.name.ifBlank { b.ringId } + "?") }, confirmButton = { TextButton(onClick = { vm.delete(b); delete = null }) { Text("Hapus", color = Color.Red) } }, dismissButton = { TextButton(onClick = { delete = null }) { Text("Batal") } }) }
    error?.let { AlertDialog(onDismissRequest = { error = null }, title = { Text("Gagal") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { error = null }) { Text("OK") } }) }
}

@Composable
private fun Home(birds: List<BirdEntity>, modifier: Modifier, add: () -> Unit, go: (Int) -> Unit) {
    Column(modifier.fillMaxSize().background(Ivory).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("KENARI TASIKMALAYA", color = Forest, fontSize = 12.sp, fontWeight = FontWeight.Bold); Text("Selamat datang, Breeder", color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold); Text("Koleksi disimpan offline", color = Forest, fontSize = 13.sp) }
            Surface(color = Forest, shape = RoundedCornerShape(14.dp), modifier = Modifier.size(48.dp)) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Pets, null, tint = Gold) } }
        }
        Surface(color = Forest, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("RINGKASAN KOLEKSI", color = Gold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(birds.size.toString() + " kenari terdaftar", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { CountChip("Jantan", birds.count { it.sex == "Jantan" }); CountChip("Betina", birds.count { it.sex == "Betina" }); CountChip("Unknown", birds.count { it.sex == "Belum diketahui" }) }
            }
        }
        Text("Akses cepat", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Tile("Tambah Kenari", Icons.Default.AddCircle, Modifier.weight(1f), add); Tile("Database", Icons.Default.Pets, Modifier.weight(1f)) { go(1) } }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Tile("Geneologi", Icons.Default.AccountTree, Modifier.weight(1f)) { go(2) }; Tile("Breeding", Icons.Default.Favorite, Modifier.weight(1f)) { go(3) } }
        Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(16.dp)) { Text(if(birds.isEmpty()) "Mulai membangun silsilah" else "Data koleksi tersimpan", color = Forest, fontWeight = FontWeight.Bold); Text(if(birds.isEmpty()) "Tambah profil, foto, ring, dan pilih indukan dari koleksi." else birds.size.toString() + " profil kenari sudah tersimpan di perangkat.", color = Forest, fontSize = 13.sp) } }
    }
}
@Composable private fun CountChip(label: String, count: Int) { Surface(color = Color.White.copy(.12f), shape = RoundedCornerShape(10.dp)) { Column(Modifier.padding(9.dp)) { Text(count.toString(), color = Color.White, fontWeight = FontWeight.Bold); Text(label, color = Color.White, fontSize = 10.sp) } } }
@Composable private fun Tile(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, mod: Modifier = Modifier, click: () -> Unit) { Surface(onClick = click, modifier = mod, color = Color.White, shape = RoundedCornerShape(16.dp)) { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Icon(icon, label, tint = Forest, modifier = Modifier.size(26.dp)); Text(label, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) } } }

@Composable
private fun BirdList(birds: List<BirdEntity>, modifier: Modifier, edit: (BirdEntity) -> Unit, delete: (BirdEntity) -> Unit) {
    var query by remember { mutableStateOf("") }
    val found = birds.filter { it.name.contains(query, true) || it.ringId.contains(query, true) || it.breed.contains(query, true) }
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp)); Text("Database Kenari", color = Forest, fontSize = 24.sp, fontWeight = FontWeight.Bold); Text(birds.size.toString() + " data", color = Forest)
        OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Cari nama, ring, varietas") }, leadingIcon = { Icon(Icons.Default.Search, null) })
        if(found.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(if(birds.isEmpty()) "Belum ada data. Tekan + untuk menambah." else "Data tidak ditemukan.") }
        else LazyColumn(contentPadding = PaddingValues(bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(found, key = { it.id }) { b ->
                Surface(color = Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().clickable { edit(b) }) {
                    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        if(b.photoUri.isNullOrBlank()) Icon(Icons.Default.Pets, null, tint = Forest, modifier = Modifier.size(58.dp).padding(8.dp)) else AsyncImage(b.photoUri, "Foto", modifier = Modifier.size(64.dp))
                        Column(Modifier.weight(1f).padding(horizontal = 8.dp)) { Text(b.name.ifBlank { "Kenari " + b.ringId }, fontWeight = FontWeight.Bold, color = Ink); Text("Ring: " + b.ringId, color = Forest, fontSize = 12.sp); Text(b.sex + " • " + b.breed, color = Forest, fontSize = 12.sp); Text(age(b.hatchDate), color = Forest, fontSize = 11.sp) }
                        Column { IconButton(onClick = { edit(b) }) { Icon(Icons.Default.Edit, "Edit", tint = Forest) }; IconButton(onClick = { delete(b) }) { Icon(Icons.Default.DeleteOutline, "Hapus", tint = Color.Red) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun BirdForm(initial: BirdEntity?, birds: List<BirdEntity>, close: () -> Unit, save: (BirdEntity) -> Unit) {
    var ring by remember { mutableStateOf(initial?.ringId.orEmpty()) }; var name by remember { mutableStateOf(initial?.name.orEmpty()) }; var sex by remember { mutableStateOf(initial?.sex ?: "Belum diketahui") }; var hatch by remember { mutableStateOf(initial?.hatchDate?.let { fmt(it) }.orEmpty()) }; var breed by remember { mutableStateOf(initial?.breed.orEmpty()) }; var color by remember { mutableStateOf(initial?.colorMutation.orEmpty()) }; var origin by remember { mutableStateOf(initial?.origin.orEmpty()) }; var notes by remember { mutableStateOf(initial?.notes.orEmpty()) }; var photo by remember { mutableStateOf(initial?.photoUri) }; var father by remember { mutableStateOf(initial?.fatherId) }; var mother by remember { mutableStateOf(initial?.motherId) }; var error by remember { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { photo = it?.toString() }
    val options = birds.filter { it.id != initial?.id }
    AlertDialog(onDismissRequest = close, title = { Text(if(initial == null) "Tambah Kenari" else "Edit Kenari") }, text = {
        Column(Modifier.heightIn(max = 520.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { if(photo != null) AsyncImage(photo, "Foto kenari", modifier = Modifier.size(70.dp)) else Icon(Icons.Default.Pets, null, tint = Forest, modifier = Modifier.size(60.dp)); Button(onClick = { picker.launch("image/*") }) { Text("Foto") }; TextButton(onClick = { photo = null }) { Text("Reset") } }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                item { Field(ring, { ring = it }, "Nomor ring *") }; item { Field(name, { name = it }, "Nama") }; item { SexMenu(sex) { sex = it } }; item { Field(hatch, { hatch = it }, "Tanggal menetas yyyy-MM-dd") }; item { Field(breed, { breed = it }, "Jenis / varietas") }; item { Field(color, { color = it }, "Warna / mutasi") }; item { Field(origin, { origin = it }, "Asal breeder") }; item { ParentMenu("Indukan jantan", father, options.filter { it.sex != "Betina" }) { father = it } }; item { ParentMenu("Indukan betina", mother, options.filter { it.sex != "Jantan" }) { mother = it } }; item { OutlinedTextField(notes, { notes = it }, label = { Text("Catatan") }, minLines = 2, modifier = Modifier.fillMaxWidth()) }; if(error != null) item { Text(error!!, color = Color.Red) }
            }
        }
    }, confirmButton = { TextButton(onClick = { val date = parse(hatch); if(ring.isBlank()) error = "Nomor ring wajib diisi" else if(hatch.isNotBlank() && date == null) error = "Tanggal gunakan yyyy-MM-dd" else { error = null; save(BirdEntity(id = initial?.id ?: 0, ringId = ring.trim(), name = name.trim(), sex = sex, hatchDate = date, breed = breed.trim(), colorMutation = color.trim(), origin = origin.trim(), fatherId = father, motherId = mother, photoUri = photo, notes = notes.trim(), createdAt = initial?.createdAt ?: System.currentTimeMillis())) } }) { Text("Simpan") } }, dismissButton = { TextButton(onClick = close) { Text("Batal") } })
}
@Composable private fun Field(value: String, change: (String) -> Unit, label: String) { OutlinedTextField(value, change, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
@Composable private fun SexMenu(value: String, set: (String) -> Unit) { var open by remember { mutableStateOf(false) }; Box { OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) { Text("Jenis kelamin: " + value); Spacer(Modifier.weight(1f)); Icon(Icons.Default.ArrowDropDown, null) }; DropdownMenu(open, { open = false }) { listOf("Jantan", "Betina", "Belum diketahui").forEach { DropdownMenuItem(text = { Text(it) }, onClick = { set(it); open = false }) } } } }
@Composable private fun ParentMenu(label: String, id: Long?, list: List<BirdEntity>, set: (Long?) -> Unit) { var open by remember { mutableStateOf(false) }; val current = list.firstOrNull { it.id == id }; Box { OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) { Text(label + ": " + (current?.name?.ifBlank { current.ringId } ?: "Belum dipilih"), maxLines = 1); Spacer(Modifier.weight(1f)); Icon(Icons.Default.ArrowDropDown, null) }; DropdownMenu(open, { open = false }) { DropdownMenuItem(text = { Text("Kosong") }, onClick = { set(null); open = false }); list.forEach { b -> DropdownMenuItem(text = { Text(b.name.ifBlank { b.ringId } + " • " + b.ringId) }, onClick = { set(b.id); open = false }) } } } }
@Composable private fun GenealogyPage(birds: List<BirdEntity>, modifier: Modifier = Modifier) {
    val demoChild = birds.firstOrNull { it.ringId == "KTS-003" }
    val father = birds.firstOrNull { it.id == demoChild?.fatherId } ?: birds.firstOrNull { it.sex == "Jantan" }
    val mother = birds.firstOrNull { it.id == demoChild?.motherId } ?: birds.firstOrNull { it.sex == "Betina" }
    Column(modifier.fillMaxSize().background(Ivory).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Pohon Geneologi", color = Forest, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Contoh hubungan indukan dan anakan. Data dapat diedit melalui menu Kenari.", color = Forest, fontSize = 13.sp)
        Surface(color = Color.White, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("GENERASI INDUKAN", color = Forest, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GenealogyBirdCard(father, "♂ Jantan", Modifier.weight(1f))
                    GenealogyBirdCard(mother, "♀ Betina", Modifier.weight(1f))
                }
                Icon(Icons.Default.CallMade, "Hubungan indukan", tint = Gold, modifier = Modifier.size(28.dp))
                Text("↓ Pasangan breeding ↓", color = Forest, fontWeight = FontWeight.SemiBold)
                Text("GENERASI ANAKAN", color = Forest, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                GenealogyBirdCard(demoChild, "Anakan • KTS-003", Modifier.fillMaxWidth())
            }
        }
        Text("Semua 3 data demo otomatis masuk CRUD dan dapat diedit atau dihapus. Geneologi memakai relasi fatherId/motherId.", color = Forest, fontSize = 12.sp)
    }
}
@Composable private fun GenealogyBirdCard(bird: BirdEntity?, label: String, modifier: Modifier = Modifier) {
    Surface(color = Ivory, shape = RoundedCornerShape(14.dp), modifier = modifier) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (bird?.photoUri.isNullOrBlank()) Icon(Icons.Default.Pets, null, tint = Forest, modifier = Modifier.size(62.dp))
            else AsyncImage(bird?.photoUri, label, modifier = Modifier.size(82.dp))
            Text(label, color = Gold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(bird?.name ?: "Belum ada data", color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(bird?.ringId ?: "-", color = Forest, fontSize = 11.sp)
            Text(bird?.colorMutation ?: "", color = Forest, fontSize = 10.sp)
        }
    }
}
@Composable private fun BasicPage(title: String, description: String) { Column(Modifier.fillMaxSize().background(Ivory).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Text(title, color = Forest, fontSize = 24.sp, fontWeight = FontWeight.Bold); Text(description, color = Forest); Surface(color = Color.White, shape = RoundedCornerShape(16.dp)) { Text("Modul akan diintegrasikan bertahap.", Modifier.padding(16.dp), color = Ink) } } }
private fun parse(s: String): Long? = if(s.isBlank()) null else runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(s)?.time }.getOrNull()
private fun fmt(t: Long) = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(t))
private fun age(t: Long?): String { if(t == null) return "Umur belum diketahui"; val m = ((System.currentTimeMillis()-t)/86400000/30).coerceAtLeast(0); return if(m>=12) "Umur " + (m/12) + " tahun " + (m%12) + " bulan" else "Umur " + m + " bulan" }
