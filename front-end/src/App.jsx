import { useState } from 'react'
import './App.css'

function App() {
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [isUploading, setIsUploading] = useState(false) 


  const handleSendMessage = async () => {
    if (!input.trim()) return

    const novaMensagemUsuario = { role: 'user', content: input }
    const novasMensagens = [...messages, novaMensagemUsuario]
    
    setMessages(novasMensagens)
    setInput('')
    setIsLoading(true)
    

    try {
      const response = await fetch('http://localhost:8082/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pergunta: input })
      })

      if (!response.ok) throw new Error('Erro na rede')

      const data = await response.json()
      setMessages([...novasMensagens, { role: 'bot', content: data.resposta }])
      
    } catch (error) {
      console.error('Erro:', error)
      setMessages([...novasMensagens, { role: 'bot', content: 'Ops! O servidor parece estar offline.' }])
    } finally {
      setIsLoading(false)
    }
  }

  const handleFileUpload = async (event) => {
    const file = event.target.files[0]
    if (!file) return


    if (file.type !== 'application/pdf') {
      alert('Por favor, selecione apenas arquivos PDF.')
      return
    }


    const formData = new FormData()
    formData.append('arquivo', file) 

    setIsUploading(true)
   
    setMessages(prev => [...prev, { role: 'bot', content: `⏳ Iniciando a leitura e vetorização da apostila: ${file.name}... Isso pode demorar alguns segundos.` }])

    try {
      const response = await fetch('http://localhost:8082/api/conteudo/pdf', {
        method: 'POST',
        body: formData 
      })

      if (!response.ok) throw new Error('Erro ao enviar o arquivo')

      const resultText = await response.text() // Nosso Java retorna uma String simples no sucesso
      
      setMessages(prev => [...prev, { role: 'bot', content: `✅ Sucesso: ${resultText}` }])

    } catch (error) {
      console.error('Erro no upload:', error)
      setMessages(prev => [...prev, { role: 'bot', content: '❌ Falha ao processar o PDF. Verifique o console do Spring Boot.' }])
    } finally {
      setIsUploading(false)
      event.target.value = null 
    }
  }

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') handleSendMessage()
  }

  return (
    <div className="chat-container">
      <header className="chat-header">
        <h1>Monitor Virtual de Programação 🤖</h1>
        
        <div className="upload-section">
          <input 
            type="file" 
            id="pdf-upload" 
            accept=".pdf" 
            onChange={handleFileUpload} 
            disabled={isUploading}
            style={{ display: 'none' }} 
          />
          <label htmlFor="pdf-upload" className={`upload-btn ${isUploading ? 'disabled' : ''}`}>
            {isUploading ? 'Carregando...' : '📄 Subir Apostila (PDF)'}
          </label>
        </div>
      </header>

      <div className="chat-messages">
        {messages.length === 0 && (
          <p className="welcome-text">Olá! Faça uma pergunta ou suba a apostila didática para começarmos.</p>
        )}
        
        {messages.map((msg, index) => (
          <div key={index} className={`message ${msg.role}`}>
            <div className="message-bubble">
              {msg.content}
            </div>
          </div>
        ))}
        
        {isLoading && (
          <div className="message bot">
            <div className="message-bubble loading">Digitando...</div>
          </div>
        )}
      </div>

      <div className="chat-input-area">
        <input 
          type="text" 
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleKeyPress}
          placeholder="Digite sua dúvida aqui..."
          disabled={isLoading}
        />
        <button onClick={handleSendMessage} disabled={isLoading || !input.trim()}>
          {isLoading ? '...' : 'Enviar'}
        </button>
      </div>
    </div>
  )
}

export default App