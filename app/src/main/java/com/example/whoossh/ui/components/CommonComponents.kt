package com.example.whoossh.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshRedLight
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.utils.tr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhooshTopBar(
    title: String,
    onBack: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(text = title.tr(),
                style = MaterialTheme.typography.titleLarge,
                color = WhooshWhite
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = WhooshWhite
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WhooshRed
        )
    )
}

@Composable
fun WhooshButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "btn_scale")

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .scale(scale),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        contentPadding = PaddingValues(),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(
                    brush = if (enabled) Brush.horizontalGradient(
                        listOf(WhooshGradientStart, WhooshGradientEnd)
                    ) else Brush.horizontalGradient(
                        listOf(Color.Gray, Color.Gray)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = WhooshWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = WhooshWhite,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WhooshOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.5.dp, WhooshRed)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = WhooshRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = WhooshRed,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun WhooshCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WhooshWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhooshDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label.tr()) },
            leadingIcon = if (leadingIcon != null) {
                { Icon(leadingIcon, contentDescription = null, tint = WhooshRed) }
            } else null,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WhooshRed,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = WhooshRed
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.tr()) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TicketStepper(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = if (count > 1) WhooshRedLight.copy(alpha = 0.12f) else Color.LightGray.copy(alpha = 0.4f),
            onClick = onDecrement
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Remove,
                    contentDescription = "Kurangi",
                    tint = if (count > 1) WhooshRed else Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Text(
            text = "$count".tr(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.Center
        )

        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = if (count < 10) WhooshRedLight.copy(alpha = 0.12f) else Color.LightGray.copy(alpha = 0.4f),
            onClick = onIncrement
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Tambah",
                    tint = if (count < 10) WhooshRed else Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(text = title.tr(),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label.tr(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(40) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(1.dp)
                        .background(Color.LightGray)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
