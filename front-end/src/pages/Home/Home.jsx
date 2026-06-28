import styles from './Home.module.css'

function Home() {
  return (
    <div className={styles.container}>
      <h2>Bem-vindo ao Monitor Virtual</h2>
      <p>Faça sua pergunta para a IA.</p>
    </div>
  )
}

export default Home