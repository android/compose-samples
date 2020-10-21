package com.example.jetsnack.ui.components

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ChainStyle
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.preferredWidthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.example.jetsnack.R
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun QuantitySelector(
    count: Int,
    updateCount: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (qty, minus, quantity, plus) = createRefs()
        createHorizontalChain(qty, minus, quantity, plus, chainStyle = ChainStyle.Packed)
        Text(
            text = stringResource(R.string.quantity),
            style = MaterialTheme.typography.subtitle1,
            color = JetsnackTheme.colors.textSecondary,
            modifier = Modifier.constrainAs(qty) {
                start.linkTo(parent.start)
                linkTo(top = parent.top, bottom = parent.bottom)
            }
        )
        JetsnackGradientTintedIconButton(
            asset = Icons.Outlined.RemoveCircleOutline,
            onClick = { if (count > 0) updateCount(count - 1) },
            modifier = Modifier.constrainAs(minus) {
                centerVerticallyTo(quantity)
                linkTo(top = parent.top, bottom = parent.bottom)
            }
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.subtitle2,
            fontSize = 18.sp,
            color = JetsnackTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.preferredWidthIn(min = 24.dp).constrainAs(quantity) {
                baseline.linkTo(qty.baseline)
            }
        )
        JetsnackGradientTintedIconButton(
            asset = Icons.Outlined.AddCircleOutline,
            onClick = { updateCount(count + 1) },
            modifier = Modifier.constrainAs(plus) {
                end.linkTo(parent.end)
                centerVerticallyTo(quantity)
                linkTo(top = parent.top, bottom = parent.bottom)
            }
        )
    }
}

@Preview
@Composable
fun QuantitySelectorPreview() {
    JetsnackTheme {
        JetsnackSurface {
            QuantitySelector(1, {})
        }
    }
}