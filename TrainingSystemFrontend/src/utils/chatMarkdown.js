function escapeHtml(str) {
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}

function formatInline(str) {
  return str.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
}

export function renderChatAnswer(text) {
  if (!text) return ''
  const lines = escapeHtml(text).split(/\r?\n/)
  let html = ''
  let inList = false
  for (const rawLine of lines) {
    const line = rawLine.trim()
    const bulletMatch = line.match(/^[*-]\s+(.*)/)
    if (bulletMatch) {
      if (!inList) {
        html += '<ul>'
        inList = true
      }
      html += `<li>${formatInline(bulletMatch[1])}</li>`
      continue
    }
    if (inList) {
      html += '</ul>'
      inList = false
    }
    if (line) {
      html += `<p>${formatInline(line)}</p>`
    }
  }
  if (inList) html += '</ul>'
  return html
}
