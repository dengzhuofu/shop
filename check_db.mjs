import pg from 'pg';
const { Client } = pg;

const client = new Client({
  user: 'postgres',
  host: 'localhost',
  database: 'shop',
  password: '1st1st1st',
  port: 5432,
});

async function checkDB() {
  try {
    await client.connect();
    console.log('Connected to PostgreSQL successfully.');

    const res = await client.query(`
      SELECT table_name, column_name, data_type 
      FROM information_schema.columns 
      WHERE table_schema = 'public'
      ORDER BY table_name, ordinal_position;
    `);

    const tables = {};
    res.rows.forEach(row => {
      if (!tables[row.table_name]) {
        tables[row.table_name] = [];
      }
      tables[row.table_name].push(row.column_name);
    });

    console.log(JSON.stringify(tables, null, 2));

  } catch (err) {
    console.error('Error connecting to or querying the database:', err);
  } finally {
    await client.end();
  }
}

checkDB();
