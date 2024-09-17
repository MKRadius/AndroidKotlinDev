package com.example.lab06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab06.ui.theme.Lab06Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab06Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier.padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ParliamentMemberComponentsGroup()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParliamentMemberComponentsGroup() {
    var memberData by remember { mutableStateOf(ParliamentMembersData.members.randomOrNull()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        memberData?.let { ParliamentMemberCard(it) } ?: Text("No member found")
        Spacer(modifier = Modifier.height(16.dp))
        RandomButton { memberData = ParliamentMembersData.members.randomOrNull() }
    }
}

@Composable
fun ParliamentMemberCard(data: ParliamentMember) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(150.dp)
            .clip(CircleShape)
            .background(Color.Gray)
            .border(2.dp, Color.Gray, CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.placeholder),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
    if (data.minister) Text("Minister") else Spacer(modifier = Modifier.height(24.dp))
    Text("${data.firstname} ${data.lastname}", style = TextStyle(fontSize = 24.sp))
    Text("Heteka ID: ${data.hetekaId}")
    Text("Party: ${data.party.uppercase()}")
    Text("Seat: ${data.seatNumber}")
}

@Composable
fun RandomButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(40.dp)
    ) {
        Text("Random Member")
    }
}