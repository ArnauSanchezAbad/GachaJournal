package com.udg.gachajournal.ui.gacha

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udg.gachajournal.data.CosmeticGacha
import com.udg.gachajournal.ui.AppTheme
import kotlin.random.Random

@Composable
fun GachaScreen(viewModel: GachaViewModel, theme: AppTheme) {
    val uiState by viewModel.uiState.collectAsState()
    val multiRollCost = SINGLE_ROLL_COST * 10

    val fontColor = theme.fontColor?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.White

    Box(modifier = Modifier.fillMaxSize()) {
        StarryBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center all content vertically
        ) {
            Text(
                text = "Gacha Points: ${uiState.user?.gachaPoints ?: 0}",
                color = fontColor,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            GachaMachine(uiState = uiState, fontColor = fontColor)

            Spacer(modifier = Modifier.height(32.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val buttonColors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE),
                        disabledContainerColor = Color.Gray
                    )

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.performSingleRoll() },
                        enabled = (uiState.user?.gachaPoints ?: 0) >= SINGLE_ROLL_COST && !uiState.isRolling,
                        colors = buttonColors
                    ) {
                        Text(text = "Tirar 1 (${SINGLE_ROLL_COST} GP)")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.performMultiRoll() },
                        enabled = (uiState.user?.gachaPoints ?: 0) >= multiRollCost && !uiState.isRolling,
                        colors = buttonColors
                    ) {
                        Text(text = "Tirar 10 (${multiRollCost} GP)")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                PityInfo(uiState = uiState, fontColor = fontColor)
            }
        }
    }
}

@Composable
fun GachaMachine(uiState: GachaUiState, fontColor: Color) {
    Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
        if (uiState.isRolling) {
            CircularProgressIndicator()
        } else if (uiState.showResult) {
            uiState.result?.let {
                Text(text = "Has aconseguit: ${it.name}", color = fontColor, fontSize = 22.sp)
            } ?: Text(text = "Ja ho tens tot!", color = fontColor, fontSize = 18.sp)
        } else {
            Text(text = "Prepara't per a la tirada!", color = fontColor, fontSize = 18.sp)
        }
    }
}

@Composable
fun PityInfo(uiState: GachaUiState, fontColor: Color) {
    val user = uiState.user ?: return

    val remainingEpic = CosmeticGacha.PITY_EPIC - user.gachaRollsSinceLast4Star
    val remainingLegendary = CosmeticGacha.PITY_LEGENDARY - user.gachaRollsSinceLast5Star

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Recompensa Assegurada", color = fontColor, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "⭐⭐⭐⭐ Èpic - Assegurat en $remainingEpic tirades",
            color = fontColor
        )
        Text(
            text = "⭐⭐⭐⭐⭐ Legendari - Assegurat en $remainingLegendary tirades",
            color = fontColor
        )
    }
}

@Composable
fun StarryBackground() {
    val starPoints = remember {
        List(200) { 
            Offset(x = Random.nextFloat(), y = Random.nextFloat())
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = Color(0xFF1A0A2A))
        starPoints.forEach {
            drawCircle(
                color = Color.White.copy(alpha = Random.nextFloat()),
                radius = Random.nextFloat() * 2 + 1,
                center = Offset(it.x * size.width, it.y * size.height)
            )
        }
    }
}