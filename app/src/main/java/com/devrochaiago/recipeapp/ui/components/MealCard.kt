package com.devrochaiago.recipeapp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.devrochaiago.recipeapp.R
import com.devrochaiago.recipeapp.data.remote.MealDto
import com.devrochaiago.recipeapp.util.shimmerEffect

@Composable
fun MealCard(
    meal: MealDto,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onNavigateToDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Lado Esquerdo: Textos
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meal.category?.uppercase() ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!meal.instructions.isNullOrBlank()) {
                        Text(
                            text = meal.instructions,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (expanded) 10 else 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Justify
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Ícone de Expansão destacado
                        FilledTonalIconButton(
                            onClick = { expanded = !expanded },
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(id = R.string.meal_card_expand),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        // Caso não haja descrição (comum em filtros por categoria/área)
                        Text(
                            text = stringResource(id = R.string.meal_card_no_instructions),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp)) // Aumentado para evitar compressão

                // Lado Direito: Imagem e Ações fixas
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(120.dp) // Largura fixa garantida
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        AsyncImage(
                            model = meal.thumbnail,
                            contentDescription = meal.name,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )

                        IconButton(
                            onClick = onToggleFavorite,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.padding(4.dp).size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = stringResource(id = R.string.meal_card_favorite),
                                modifier = Modifier.size(18.dp),
                                tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botão de Ação Principal (Explore) fixo abaixo da imagem
                    Button(
                        onClick = onNavigateToDetail,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.meal_card_action_button),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MealCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.fillMaxWidth(0.3f).height(12.dp).shimmerEffect())
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth(0.8f).height(24.dp).shimmerEffect())
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth(0.9f).height(40.dp).shimmerEffect())
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).shimmerEffect())
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(120.dp).clip(RoundedCornerShape(20.dp)).shimmerEffect())
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.width(120.dp).height(36.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                }
            }
        }
    }
}
